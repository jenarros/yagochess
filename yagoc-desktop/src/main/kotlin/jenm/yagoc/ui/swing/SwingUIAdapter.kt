package jenm.yagoc.ui.swing

import jenm.yagoc.ui.UIAdapter
import javax.swing.SwingUtilities

class SwingUIAdapter : UIAdapter {
    override fun invokeLater(runnable: Runnable) {
        SwingUtilities.invokeLater(runnable)
    }
}