package voice.settings.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squareup.anvil.annotations.ContributesTo
import voice.common.AppScope
import voice.common.compose.VoiceTheme
import voice.common.compose.rememberScoped
import voice.common.rootComponentAs
import voice.settings.SettingsListener
import voice.settings.SettingsViewModel
import voice.settings.SettingsViewState
import voice.strings.R as StringsR

@Composable
@Preview
private fun SettingsPreview() {
  val viewState = SettingsViewState(
    useDarkTheme = false,
    showDarkThemePref = true,
    seekTimeInSeconds = 42,
    autoRewindInSeconds = 12,
    dialog = null,
    appVersion = "1.2.3",
    useGrid = true,
    autoSleepStartTime = "22:00",
    autoSleepEndTime = "06:00",
    autoSleepDurationMinutes = "20",
    isAutoSleepEnabled = false,
  )
  VoiceTheme {
    Settings(
      viewState,
      object : SettingsListener {
        override fun close() {}
        override fun toggleDarkTheme() {}
        override fun seekAmountChanged(seconds: Int) {}
        override fun onSeekAmountRowClick() {}
        override fun autoRewindAmountChang(seconds: Int) {}
        override fun onAutoRewindRowClick() {}
        override fun dismissDialog() {}
        override fun openTranslations() {}
        override fun getSupport() {}
        override fun suggestIdea() {}
        override fun openBugReport() {}
        override fun toggleGrid() {}
        override fun onAutoSleepStartTimeChange(startTime: String) {}
        override fun onAutoSleepEndTimeChange(endTime: String) {}
        override fun onAutoSleepDurationChange(durationMinutes: Int) {}
        override fun saveAutoSleepSettings() {}
        override fun toggleAutoSleep(enabled: Boolean) {}
      },
    )
  }
}

@Composable
private fun Settings(
  viewState: SettingsViewState,
  listener: SettingsListener,
) {
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
  val showAutoSleepDialog = remember { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
          Text(stringResource(StringsR.string.action_settings))
        },
        navigationIcon = {
          IconButton(
            onClick = {
              listener.close()
            },
          ) {
            Icon(
              imageVector = Icons.Outlined.Close,
              contentDescription = stringResource(StringsR.string.close),
            )
          }
        },
      )
    },
  ) { contentPadding ->
    Box(Modifier.padding(contentPadding)) {
      Column(
        Modifier
          .verticalScroll(rememberScrollState())
          .nestedScroll(scrollBehavior.nestedScrollConnection)
          .padding(vertical = 8.dp),
      ) {
        if (viewState.showDarkThemePref) {
          DarkThemeRow(viewState.useDarkTheme, listener::toggleDarkTheme)
        }
        ListItem(
          modifier = Modifier.clickable { listener.toggleGrid() },
          leadingContent = {
            val imageVector = if (viewState.useGrid) {
              Icons.Outlined.GridView
            } else {
              Icons.AutoMirrored.Outlined.ViewList
            }
            Icon(imageVector, stringResource(StringsR.string.pref_use_grid))
          },
          headlineContent = { Text(stringResource(StringsR.string.pref_use_grid)) },
          trailingContent = {
            Switch(
              checked = viewState.useGrid,
              onCheckedChange = {
                listener.toggleGrid()
              },
            )
          },
        )
        SeekTimeRow(viewState.seekTimeInSeconds) {
          listener.onSeekAmountRowClick()
        }
        AutoRewindRow(viewState.autoRewindInSeconds) {
          listener.onAutoRewindRowClick()
        }
        ListItem(
          modifier = Modifier.clickable { listener.suggestIdea() },
          leadingContent = { Icon(Icons.Outlined.Lightbulb, stringResource(StringsR.string.pref_suggest_idea)) },
          headlineContent = { Text(stringResource(StringsR.string.pref_suggest_idea)) },
        )
        ListItem(
          modifier = Modifier.clickable { listener.getSupport() },
          leadingContent = { Icon(Icons.AutoMirrored.Outlined.HelpOutline, stringResource(StringsR.string.pref_get_support)) },
          headlineContent = { Text(stringResource(StringsR.string.pref_get_support)) },
        )
        ListItem(
          modifier = Modifier.clickable { listener.openBugReport() },
          leadingContent = { Icon(Icons.Outlined.BugReport, stringResource(StringsR.string.pref_report_issue)) },
          headlineContent = { Text(stringResource(StringsR.string.pref_report_issue)) },
        )
        ListItem(
          modifier = Modifier.clickable { listener.openTranslations() },
          leadingContent = { Icon(Icons.Outlined.Language, stringResource(StringsR.string.pref_help_translating)) },
          headlineContent = { Text(stringResource(StringsR.string.pref_help_translating)) },
        )
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(StringsR.string.pref_auto_sleep_timer),
            modifier = Modifier.weight(1f).clickable { showAutoSleepDialog.value = true }
          )
          IconButton(onClick = { showAutoSleepDialog.value = true }) {
            Icon(
              imageVector = Icons.Outlined.Settings,
              contentDescription = stringResource(StringsR.string.pref_auto_sleep_timer_settings)
            )
          }
          Switch(
            checked = viewState.isAutoSleepEnabled,
            onCheckedChange = listener::toggleAutoSleep
          )
        }
        AutoSleepTimerSettingsDialog(
          isVisible = showAutoSleepDialog.value,
          onDismiss = { showAutoSleepDialog.value = false },
          isAutoSleepEnabled = viewState.isAutoSleepEnabled,
          onAutoSleepToggle = listener::toggleAutoSleep,
          startTime = viewState.autoSleepStartTime,
          onStartTimeChange = listener::onAutoSleepStartTimeChange,
          endTime = viewState.autoSleepEndTime,
          onEndTimeChange = listener::onAutoSleepEndTimeChange,
          durationMinutes = viewState.autoSleepDurationMinutes,
          onDurationChange = listener::onAutoSleepDurationChange,
          onSave = {
            listener.saveAutoSleepSettings()
            showAutoSleepDialog.value = false
          },
        )
        AppVersion(appVersion = viewState.appVersion)
        Dialog(viewState, listener)
      }
    }
  }
}

@ContributesTo(AppScope::class)
interface SettingsComponent {
  val settingsViewModel: SettingsViewModel
}

@Composable
fun Settings() {
  val viewModel = rememberScoped { rootComponentAs<SettingsComponent>().settingsViewModel }
  val viewState = viewModel.viewState()
  Settings(viewState, viewModel)
}

@Composable
private fun Dialog(
  viewState: SettingsViewState,
  listener: SettingsListener,
) {
  val dialog = viewState.dialog ?: return
  when (dialog) {
    SettingsViewState.Dialog.AutoRewindAmount -> {
      AutoRewindAmountDialog(
        currentSeconds = viewState.autoRewindInSeconds,
        onSecondsConfirm = listener::autoRewindAmountChang,
        onDismiss = listener::dismissDialog,
      )
    }
    SettingsViewState.Dialog.SeekTime -> {
      SeekAmountDialog(
        currentSeconds = viewState.seekTimeInSeconds,
        onSecondsConfirm = listener::seekAmountChanged,
        onDismiss = listener::dismissDialog,
      )
    }
  }
}

@Composable
fun AutoSleepTimerSettingsDialog(
  isVisible: Boolean,
  onDismiss: () -> Unit,
  isAutoSleepEnabled: Boolean,
  onAutoSleepToggle: (Boolean) -> Unit,
  startTime: String,
  onStartTimeChange: (String) -> Unit,
  endTime: String,
  onEndTimeChange: (String) -> Unit,
  durationMinutes: String,
  onDurationChange: (String) -> Unit,
  onSave: () -> Unit,
) {
  if (isVisible) {
    AlertDialog(
      onDismissRequest = onDismiss,
      confirmButton = {
        Button(onClick = onSave) {
          Text(stringResource(StringsR.string.save))
        }
      },
      dismissButton = {
        Button(onClick = onDismiss) {
          Text(stringResource(StringsR.string.cancel))
        }
      },
      title = {
        Text(stringResource(StringsR.string.pref_auto_sleep_timer_settings))
      },
      text = {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
              checked = isAutoSleepEnabled,
              onCheckedChange = onAutoSleepToggle,
            )
            Text(stringResource(StringsR.string.pref_auto_sleep_timer))
          }
          if (isAutoSleepEnabled) {
            OutlinedTextField(
              value = startTime,
              onValueChange = onStartTimeChange,
              label = { Text(stringResource(StringsR.string.pref_auto_sleep_timer_start_time)) },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = endTime,
              onValueChange = onEndTimeChange,
              label = { Text(stringResource(StringsR.string.pref_auto_sleep_timer_end_time)) },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = durationMinutes,
              onValueChange = onDurationChange,
              label = { Text(stringResource(StringsR.string.pref_auto_sleep_timer_duration_minutes)) },
              placeholder = { Text(stringResource(StringsR.string.minutes)) },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    )
  }
}
