package net.ruhamaa.mobile.data.repsitory

import net.ruhamaa.mobile.data.Result
import net.ruhamaa.mobile.data.getOrNull
import net.ruhamaa.mobile.data.model.Case
import net.ruhamaa.mobile.data.model.Empty
import net.ruhamaa.mobile.data.source.RuhamaDataSource
import net.ruhamaa.mobile.data.source.remote.getFakeImages
import net.ruhamaa.mobile.data.toSuccess
import java.lang.Exception

class RuhamaaRepositoryImpl(private val ruhamaDataSource: RuhamaDataSource) : RuhamaaRepository {
    val cachedCases = mutableListOf<Case>()

    override suspend fun getCases(forceUpdate: Boolean): Result<List<Case>> {
        val result = ruhamaDataSource.getCases()
        result.getOrNull()?.let { cases ->
            cachedCases.clear()
            cachedCases.addAll(cases.map { it.copy(otherImages = getFakeImages()) })
        }
        return if (cachedCases.isNotEmpty()) cachedCases.toSuccess() else result
    }

    override suspend fun getCase(id: Int, forceUpdate: Boolean): Result<Case> {
        val case = cachedCases.singleOrNull { it.id == id }
        return case?.toSuccess() ?: Result.Error(Exception("case not found"))
    }

    override suspend fun addCase(case: Case): Result<Empty> {
        return ruhamaDataSource.addCase(case)
    }

    override suspend fun updateCase(case: Case): Result<Empty> {
        return ruhamaDataSource.updateCase(case)
    }
}