package rejasupotaro.arxiv.reader.data.repository

import android.arch.lifecycle.LiveData
import rejasupotaro.arxiv.reader.OpenClassOnDebug
import rejasupotaro.arxiv.reader.data.api.ResponseConverter
import rejasupotaro.arxiv.reader.data.db.ArxivDb
import rejasupotaro.arxiv.reader.data.http.HttpClient
import rejasupotaro.arxiv.reader.data.model.Paper
import rejasupotaro.arxiv.reader.extensions.observable
import java.io.File

@OpenClassOnDebug
class PaperRepository(private val db: ArxivDb, private val httpClient: HttpClient) {
    fun delete(paper: Pair<Paper, File>): LiveData<Unit> {
        return observable {
            paper.second.delete()
            db.paperDao().delete(paper.first)
        }
    }

    fun all(): LiveData<List<Paper>> {
        return observable {
            db.paperDao().findAll()
        }
    }

    fun findById(paperId: Long): LiveData<Paper?> {
        return observable {
            db.paperDao().findById(paperId)
        }
    }

    fun findByTitle(title: String): LiveData<Paper?> {
        return observable {
            db.paperDao().findByTitle(title)
        }
    }

    fun update(paper: Paper): LiveData<Paper> {
        return observable {
            db.paperDao().update(paper)
            paper
        }
    }

    fun similarPapers(paperId: Long): LiveData<List<Pair<Paper, Double>>> {
        return observable {
            db.paperSimilarityDao().findByFromPaperId(paperId).let { paperSimilarities ->
                db.paperDao().findByIds(paperSimilarities.map { paperSimilarity ->
                    paperSimilarity.toPaperId
                }).mapIndexed { index, paper ->
                    Pair(paper, paperSimilarities[index].similarity)
                }
            }
        }
    }

    fun search(searchRequest: SearchRequest): SearchResponse {
        val (query, page, perPage) = searchRequest
        val url = "http://export.arxiv.org/api/query?search_query=all:$query&start=${page * perPage}"
        val response = httpClient.get(url)
        val body = response?.body()?.string() ?: ""
        return if (body.isEmpty()) {
            SearchResponse(query, listOf(), page, 0)
        } else {
            ResponseConverter
                    .xmlToApiResponse(body)
                    .let {
                        SearchResponse(
                                query,
                                it.papers.map { Paper.entityToModel(it) },
                                page,
                                it.totalResults.content
                        )
                    }
        }
    }

    fun download(paper: Paper, file: File) {
        val response = httpClient.get(paper.downloadUrl)
        response.body()?.byteStream()?.use {
            it.copyTo(file.outputStream())
        }
    }
}

data class SearchRequest(val query: String, val page: Int, val perPage: Int = 20)
data class SearchResponse(val query: String, val result: List<Paper>, val page: Int, val totalPages: Int)

