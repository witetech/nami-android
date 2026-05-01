@file:Suppress("SpellCheckingInspection")

package chat.nami.auth.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import chat.nami.auth.data.mapper.UserMapper
import chat.nami.auth.data.model.UserDto
import chat.nami.auth.domain.model.User
import chat.nami.auth.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

private const val WEB_CLIENT_ID =
    "1075308777416-n79brben8kpfujhtakfd4ibroak6gcbk.apps.googleusercontent.com"

internal class RealAuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val credentialManager: CredentialManager,
    private val userMapper: UserMapper
) : AuthRepository {

    override suspend fun getUser(): User? {
        val uid = firebaseAuth.currentUser?.uid ?: return null
        return suspendCancellableCoroutine { continuation ->
            firebaseFirestore.collection("users").document(uid).get()
                .addOnSuccessListener { result ->
                    val userDto = result.toObject<UserDto>()
                    continuation.resume(userMapper.mapToUser(uid, userDto!!))
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun loginWithGoogle(activityContext: Context): User {
        val googleIdOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context = activityContext, request = request)
        val credential = result.credential

        assert(credential is CustomCredential)
        assert(credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithCredential(
                GoogleAuthProvider.getCredential(
                    googleIdTokenCredential.idToken,
                    null
                )
            ).addOnSuccessListener {
                val uid = firebaseAuth.currentUser?.uid
                if (uid != null) {
                    firebaseFirestore.collection("users").document(uid).get()
                        .addOnSuccessListener { result ->
                            val userDto = result.toObject<UserDto>()
                            continuation.resume(userMapper.mapToUser(uid, userDto!!))
                        }.addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                } else {
                    continuation.resumeWithException(IllegalStateException())
                }
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}
