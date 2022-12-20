package app.emmabritton.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.emmabritton.android_fsm.AndroidMainThreadMarshaller
import app.emmabritton.fsm.*
import app.emmabritton.notes.flow.Render
import app.emmabritton.notes.flow.common.CommonUiState
import app.emmabritton.notes.fsm.AppRuntime
import app.emmabritton.notes.fsm.UiState
import app.emmabritton.notes.ui.theme.NotesTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uiState = MutableStateFlow<UiState>(CommonUiState.Loading)
        val runtime = AppRuntime { uiState.value = it }

        setContent {
            NotesTheme {
                val state = uiState.collectAsState().value
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Render(state, runtime, Modifier.fillMaxSize())
                }
            }
        }
    }
}
