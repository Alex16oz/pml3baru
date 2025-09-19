package yuga.ridho.pml3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import yuga.ridho.pml3.ui.theme.Pml3baruTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FirebaseDatabase.getInstance().getReference("TabelMahasiswa")

        setContent {
            Pml3baruTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MahasiswaScreen(db)
                }
            }
        }
    }
}

@Composable
fun MahasiswaScreen(db: DatabaseReference) {
    var nim by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var mahasiswaList by remember { mutableStateOf<List<Mahasiswa>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Mahasiswa>()
                for (data in snapshot.children) {
                    val mahasiswa = data.getValue(Mahasiswa::class.java)
                    if (mahasiswa != null) {
                        list.add(mahasiswa)
                    }
                }
                mahasiswaList = list
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Connection to database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama Mahasiswa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = alamat,
            onValueChange = { alamat = it },
            label = { Text("Alamat") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (nim.isNotBlank() && nama.isNotBlank() && alamat.isNotBlank()) {
                    val mahasiswa = Mahasiswa(nim, nama, alamat)
                    db.child(nim).setValue(mahasiswa).addOnSuccessListener {
                        Toast.makeText(context, "Insert Success!", Toast.LENGTH_SHORT).show()
                        nim = ""
                        nama = ""
                        alamat = ""
                    }
                } else {
                    Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Insert")
            }
            Button(onClick = {
                if (nim.isNotBlank()) {
                    val updateData = mapOf(
                        "namaMhs" to nama,
                        "alamatMhs" to alamat
                    )
                    db.child(nim).updateChildren(updateData).addOnSuccessListener {
                        Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show()
                        nim = ""
                        nama = ""
                        alamat = ""
                    }
                }
            }) {
                Text("Update")
            }
            Button(onClick = {
                if (nim.isNotBlank()) {
                    db.child(nim).removeValue().addOnSuccessListener {
                        Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show()
                        nim = ""
                        nama = ""
                        alamat = ""
                    }
                }
            }) {
                Text("Delete")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(mahasiswaList) { mahasiswa ->
                MahasiswaItem(mahasiswa = mahasiswa) {
                    nim = mahasiswa.nim ?: ""
                    nama = mahasiswa.namaMhs ?: ""
                    alamat = mahasiswa.alamatMhs ?: ""
                }
            }
        }
    }
}

@Composable
fun MahasiswaItem(mahasiswa: Mahasiswa, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "NIM: ${mahasiswa.nim}")
            Text(text = "Nama: ${mahasiswa.namaMhs}")
            Text(text = "Alamat: ${mahasiswa.alamatMhs}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Pml3baruTheme {
        // This is a preview and won't connect to Firebase
        // You can create a dummy screen for preview purposes if needed
    }
}