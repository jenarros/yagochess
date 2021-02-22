package jenm.yagoc.ui

import java.util.concurrent.FutureTask
import java.util.concurrent.RunnableFuture

interface UIAdapter {
    fun invokeLater(runnable: Runnable): RunnableFuture<Unit>
}

val syncAdapter = object : UIAdapter {
    override fun invokeLater(runnable: Runnable): RunnableFuture<Unit> {
        val futureTask = FutureTask {
            runnable.run()
        }
        futureTask.run()
        return futureTask
    }
}