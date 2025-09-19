package yuga.ridho.pml3

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Enable Firebase database persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}