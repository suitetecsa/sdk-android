package cu.suitetecsa.sdkandroid.presentation.balance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.suitetecsa.sdk.android.model.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsBottomSheet(
    contacts: List<Contact> = listOf(),
    isSheetOpen: Boolean = false,
    onSetSheetOpen: (Boolean) -> Unit = {},
    onContactClick: (Contact) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { onSetSheetOpen(false) },
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BottomSheetDefaults.DragHandle()
                    Text(
                        text = "Contactos",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        value = "",
                        onValueChange = {},
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                }
            }
        ) {
            SheetContent(contacts = contacts, onContactClick = onContactClick)
        }
    }
}

@Composable
fun SheetContent(
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit
) {
    LazyColumn(Modifier.sizeIn(maxHeight = 480.dp), contentPadding = PaddingValues(16.dp)) {
        items(contacts) {
            Contact(contact = it, onClick = onContactClick)
        }
    }
}

@Composable
fun Contact(
    contact: Contact,
    onClick: (Contact) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(contact) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactImage(photoUriString = contact.photoUri)
        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = contact.phoneNumber,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactPreview() {
    Contact(contact = Contact("PortalUsuario", "phoneNumber", null)) {}
}
