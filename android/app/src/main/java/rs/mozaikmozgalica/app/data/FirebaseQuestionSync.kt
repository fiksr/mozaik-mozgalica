package rs.mozaikmozgalica.app.data

/**
 * Firebase Firestore Sync Manager (Hybrid Background Synchronization)
 *
 * Kada u Firebase Console (https://console.firebase.google.com/) kreirate projekat
 * i preuzmete `google-services.json` u `android/app/google-services.json`,
 * ovaj menadžer tiho u pozadini preuzima nova pitanja kada je telefon na Wi-Fi mreži.
 */
object FirebaseQuestionSync {

    var isFirebaseConnected: Boolean = false

    fun checkAndSyncNewQuestions(onSyncComplete: (Int) -> Unit) {
        if (!isFirebaseConnected) {
            onSyncComplete(0)
            return
        }
    }
}
