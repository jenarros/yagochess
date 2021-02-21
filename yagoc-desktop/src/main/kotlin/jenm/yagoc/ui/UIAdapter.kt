package jenm.yagoc.ui

interface UIAdapter {
    fun invokeLater(runnable: Runnable)
}

val syncAdapter = object : UIAdapter {
    override fun invokeLater(runnable: Runnable) {
        runnable.run()
    }
}