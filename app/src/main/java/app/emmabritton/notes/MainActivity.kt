package app.emmabritton.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.emmabritton.android_fsm.AndroidMainThreadMarshaller
import app.emmabritton.fsm.*
import app.emmabritton.notes.ui.theme.NotesTheme

class ScreenState : ForegroundState {
    override fun getForegroundCommands(): List<Command> {
        TODO("Not yet implemented")
    }

    override fun getCommandIdsToCancelOnBackground(): List<CommandId> {
        TODO("Not yet implemented")
    }

}

class AppState : State<ScreenState> {
    override fun getForegroundState(): ScreenState {
        TODO("Not yet implemented")
    }

}

class AppUiState : UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reduce: (Action, AppState) -> Effect<ScreenState, AppState> =
            { _, _ -> Effect(AppState(), emptyList()) }
        val kernel = RuntimeKernel(
            AndroidMainThreadMarshaller(),
            reduce,
            {},
            FixedThreadPoolExecutorCommandHandler(),
            object : Transformer<ScreenState, AppState, AppUiState> {
                override fun transform(state: AppState) = AppUiState()
            },
            AppState()
        )
        kernel.receive(object: Action{})

        setContent {
            NotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotesTheme {
        Greeting("Android")
    }
}