package bitspittle.nosweat.frontend.screens.support

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class ApplicationScope : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext = job
}