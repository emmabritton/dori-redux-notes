package app.emmabritton.fsm

import app.emmabritton.fsm.internal.createThreadedRuntime
import app.emmabritton.fsm.internal.mockAction
import org.junit.Test
import java.lang.Thread.sleep

class CancellingTests {
    @Test
    fun `GIVEN command WHEN cancelled THEN action is not received`() {
        val startCommand = mockAction()
        val testAction = mockAction()
        class TestCommand: BaseCommand() {
            override fun internalRun(actionReceiver: ActionReceiver) {
                sleep(500)
                actionReceiver.receive(testAction)
            }
        }
        val otherActionsReceived = mutableListOf<String>()
        val reduce = {action: Action, state: EmptyState ->
            when (action) {
                startCommand -> Effect(state, listOf(TestCommand()))
                else -> {
                    otherActionsReceived.add(action.describe())
                    Effect(state, emptyList())
                }
            }
        }
        val (runtime, marshaller, handler) = createThreadedRuntime(reduce, EmptyState())

        runtime.receive(startCommand)
        sleep(100)
        runtime.cancelCommands(TestCommand::class.java)
        marshaller.waitForAllTasks(2)
        handler.waitForAllTasks(2)

        assert(otherActionsReceived.isEmpty())
    }
}