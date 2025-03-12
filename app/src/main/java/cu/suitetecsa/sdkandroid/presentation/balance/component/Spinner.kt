package cu.suitetecsa.sdkandroid.presentation.balance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T> Spinner(
    items: List<T>,
    selectedItem: T,
    onItemSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    dropDownModifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedItemFactory: @Composable (T, Modifier) -> Unit,
    dropdownItemFactory: @Composable (T, Int) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        selectedItemFactory(
            selectedItem,
            Modifier
                .clickable(enabled = enabled) { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = dropDownModifier
        ) {
            items.forEachIndexed { index, element ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelect(items[index])
                        expanded = false
                    },
                    text = {
                        dropdownItemFactory(element, index)
                    }
                )
            }
        }
    }
}
