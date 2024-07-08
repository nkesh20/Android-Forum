import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val auth: FirebaseAuth) {

    val currentUser = auth.currentUser

    fun signOut() {
        auth.signOut()
    }
}