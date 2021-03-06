package rejasupotaro.arxiv.reader.ui.paper.read

import dagger.Module
import dagger.Provides
import rejasupotaro.arxiv.reader.data.repository.PaperRepository

@Module
class PaperReadActivityModule {
    @Provides
    fun providePaperViewViewModel(paperRepository: PaperRepository): PaperReadViewModel {
        return PaperReadViewModel(paperRepository)
    }
}
