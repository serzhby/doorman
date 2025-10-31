package com.github.serzhby.tools.plugins.doorman.actions

import com.github.serzhby.tools.plugins.doorman.BoundaryBundle
import com.github.serzhby.tools.plugins.doorman.model.BoundaryTarget
import com.github.serzhby.tools.plugins.doorman.model.ConnectionResponse
import com.github.serzhby.tools.plugins.doorman.services.BoundaryService
import com.github.serzhby.tools.plugins.doorman.services.Host
import com.github.serzhby.tools.plugins.doorman.ui.toolwindow.BoundaryTree
import com.intellij.credentialStore.OneTimeString
import com.intellij.database.Dbms
import com.intellij.database.access.DatabaseCredentials
import com.intellij.database.dataSource.DataSourceStorage
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.util.DbImplUtilCore
import com.intellij.database.util.common.isNotNullOrEmpty
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.EDT
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withModalProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateDataSourceAction(
  private val cs: CoroutineScope,
) : AnAction(
  "Create Data Source",
  "Create a data source for a Boundary target",
  AllIcons.General.Add
) {

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = e.getData(BoundaryTree.selectedNodeDataKey) == "target"
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val host = e.getData<Host>(DataKey.create("host"))
      ?: throw IllegalStateException("Host data not found in action event.")
    val target = e.getData<BoundaryTarget>(DataKey.create("target"))
      ?: throw IllegalStateException("BoundaryTarget data not found in action event.")

    cs.launch {
      val dataSource = withModalProgress(project, BoundaryBundle.message("dataSourceConnection.dialogTitle", target.name)) {
        val service = ApplicationManager.getApplication().getService(BoundaryService::class.java)
        withContext(Dispatchers.IO) {
          val connectionResponse = service.createConnection(host, target)
          createOrUpdateDataSource(project, connectionResponse!!, target)
        }
      }
      withContext(Dispatchers.EDT) {
        showDataSourceDialogIfPossible(project, dataSource)
        RefreshItemsAction.trigger(project)
      }
    }
  }

  private fun createOrUpdateDataSource(
    project: Project,
    connectionResponse: ConnectionResponse,
    target: BoundaryTarget
  ): LocalDataSource {
    val dbms = Dbms.fromString(target.name)
    val dataSource = DataSourceStorage.getProjectStorage(project).dataSources.firstOrNull {
      it.comment == target.id
    } ?: createEmptyDataSource(connectionResponse, dbms)
    val url = if (dataSource.url.isNotNullOrEmpty) {
      replaceHostPort(dataSource.url!!, connectionResponse.address, connectionResponse.port)
    } else {
      defaultUri(dbms, connectionResponse)
    }
    dataSource.name = target.name
    dataSource.url = url
    dataSource.username = connectionResponse.credentials.first().secret.decoded.username
    dataSource.passwordStorage = LocalDataSource.Storage.SESSION
    dataSource.comment = target.id
    DatabaseCredentials.getInstance().storePassword(
      dataSource,
      OneTimeString(connectionResponse.credentials.first().secret.decoded.password)
    )
    return dataSource
  }

  private fun showDataSourceDialogIfPossible(project: Project, dataSource: LocalDataSource) {
    try {
      val dialogClass = Class.forName("com.intellij.database.view.ui.DataSourceManagerDialog")
      val showDialog = dialogClass.methods.firstOrNull { m ->
        m.name == "showDialog" &&
          m.parameterCount == 3 &&
          Project::class.java.isAssignableFrom(m.parameterTypes[0])
      }
      showDialog?.invoke(null, project, listOf(dataSource), null)
    } catch (_: ClassNotFoundException) {
      // DataSourceManagerDialog not present on classpath — ignore
    }
  }

  private fun replaceHostPort(url: String, newHost: String, newPort: Int): String {
    val authority = Regex("(?<=://)[^/]+")
    return authority.replace(url, "$newHost:$newPort")
  }

  private fun defaultUri(dbms: Dbms, connectionResponse: ConnectionResponse): String {
    return "${defaultSchema(dbms)}://${connectionResponse.address}:${connectionResponse.port}"
  }

  private fun createEmptyDataSource(connectionResponse: ConnectionResponse, dbms: Dbms): LocalDataSource {
    val driver = DbImplUtilCore.guessDatabaseDriver(dbms)!!
    val url = defaultUri(dbms, connectionResponse)
    return LocalDataSource.fromDriver(driver, url, false)
  }

  private fun defaultSchema(dbms: Dbms): String {
    return when {
      dbms.isMysql -> "jdbc:mysql"
      dbms.isPostgres -> "jdbc:postgresql"
      dbms.isMongo -> "mongodb"
      dbms.isOracle -> "jdbc:oracle:thin"
      dbms.isSybase -> "jdbc:sybase:Tds"
      dbms.isMicrosoft -> "jdbc:sqlserver"
      dbms.isDb2 -> "jdbc:db2"
      dbms.isSqlite -> "jdbc:sqlite"
      dbms.isExasol -> "jdbc:exa"
      dbms.isBigQuery -> "jdbc:bigquery"
      dbms.isCassandra -> "jdbc:cassandra"
      dbms.isClickHouse -> "jdbc:clickhouse"
      dbms.isCouchbase -> "jdbc:couchbase"
      dbms.isDerby -> "jdbc:derby"
      dbms.isGreenplum -> "jdbc:postgresql"
      dbms.isH2 -> "jdbc:h2"
      dbms.isHive -> "jdbc:hive2"
      dbms.isHsqldb -> "jdbc:hsqldb"
      dbms.isRedshift -> "jdbc:redshift"
      dbms.isSnowflake -> "jdbc:snowflake"
      dbms.isTransactSql -> "jdbc:sqlserver"
      dbms.isVertica -> "jdbc:vertica"
      else -> "jdbc:${dbms.name.lowercase()}"
    }
  }
}