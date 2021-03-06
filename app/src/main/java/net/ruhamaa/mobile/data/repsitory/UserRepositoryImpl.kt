package net.ruhamaa.mobile.data.repsitory

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.ruhamaa.mobile.data.Result
import net.ruhamaa.mobile.data.getOrNull
import net.ruhamaa.mobile.data.model.Empty
import net.ruhamaa.mobile.data.model.User
import net.ruhamaa.mobile.data.source.UserDataSource
import net.ruhamaa.mobile.data.source.remote.responses.LoginResponse

class UserRepositoryImpl(
    private val localUserDataSource: UserDataSource,
    private val remoteUserDataSource: UserDataSource
) : UserRepository {
    val user: Flow<User?> = flow {
        while (true){
            emit(localUserDataSource.getUser().getOrNull())
            delay(1000)
        }
    }


    override suspend fun login(phoneNum: String): Result<LoginResponse> {
        return remoteUserDataSource.login(phoneNum)
    }

    override suspend fun verify(phoneNum: String, code: String): Result<User> {
        val result = remoteUserDataSource.verify(phoneNum, code)
        result.getOrNull()?.let { user ->
            localUserDataSource.updateUser(user)
        }
        return result
    }

    override suspend fun resendCode(phoneNum: String): Result<LoginResponse> {
        return remoteUserDataSource.login(phoneNum)
    }

    override suspend fun forgotPassword(phoneNum: String): Result<Empty> {
        TODO()
//        return remoteUserDataSource.forgotPassword(phoneNum)
    }

    override suspend fun updateProfile(user: User): Result<User> {
        TODO()
//        return remoteUserDataSource.updateProfile(user)
    }

    override suspend fun getUserFlow(): Flow<User?> = user

    override fun getUser() = localUserDataSource.getUser().getOrNull()

    override suspend fun updateUser(user: User): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser() = localUserDataSource.deleteUser()


}