package jenm.yagoc.ui.swing

import jenm.yagoc.ui.UIAdapter
import java.util.concurrent.RunnableFuture
import javax.swing.SwingWorker

class SwingUIAdapter : UIAdapter {
    override fun invokeLater(runnable: Runnable): RunnableFuture<Unit> {
        val worker = object : SwingWorker<Unit, Unit>() {
            override fun doInBackground() {
                runnable.run()
            }
        }
        worker.execute()
        return worker
    }
}