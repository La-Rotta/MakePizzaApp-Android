package com.example.makepizza_android.ui.view.tabs.account

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.makepizza_android.domain.models.User

import com.example.makepizza_android.ui.view.common.LoginRequired
import com.example.makepizza_android.ui.view.screens.address.list.AddressScreen
import com.example.makepizza_android.ui.view.screens.login.LoginScreen

object AccountTab : Tab {
    override val options: TabOptions
        @Composable
        get() = _GetTabOptions()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val viewModel = viewModel<AccountTabViewModel>()
        val lifecycleOwner = LocalLifecycleOwner.current
        val contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(
            NavigationBarDefaults.windowInsets
        )

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.fetchCurrentUserData()
                    Log.d("AccountTab", "Resume")
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TabToolbar(scrollBehavior = scrollBehavior) },
            contentWindowInsets = contentWindowInsets
        ) {
            TabContent(
                modifier = Modifier.padding(it),
                viewModel = viewModel
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TabToolbar(scrollBehavior: TopAppBarScrollBehavior) {
        TopAppBar(
            title = { Text(text = "Cuenta") },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.Logout, "SALIR")
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    fun TabContent(
        viewModel: AccountTabViewModel,
        modifier: Modifier = Modifier
    ) {
        val navigator = LocalNavigator.current?.parent
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        val isLoading = when (uiState) {
            AccountTabState.Loading -> true
            else -> false
        }
        val userLogged = when (uiState) {
            AccountTabState.Success(hasCurrentUser = true) -> true
            AccountTabState.Success(hasCurrentUser = false) -> false
            else -> false
        }

        if (isLoading) {
            ShowLoading(modifier = modifier)
        } else {
            if (userLogged) {
                ShowProfileInfo(modifier = modifier, viewModel = viewModel)
            } else {
                LoginRequired(toLogin = { navigator?.push(LoginScreen()) }, modifier = modifier)
            }
        }
    }

    @Composable
    private fun ShowLoading(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Cargando Datos...")
        }
    }

    @Composable
    private fun ShowProfileInfo(viewModel: AccountTabViewModel, modifier: Modifier = Modifier) {
        val current = viewModel.currentUser.observeAsState().value

        if (current != null) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                item { ProfileInfo(current) }
                item { Spacer(modifier = Modifier.height(20.dp)) }
                item { AccountOptions(current) }
                item { LegalOptions() }
                item { LogoutButton(onClick = { viewModel.handleUserLogout() }) }
            }
        }
    }

    @Composable
    private fun ProfileInfo(current: User) {
        val username = current.name
        val uid = current.uid
        val email = current.email

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = uid,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    @Composable
    private fun AccountOptions(current: User) {
        val navigator = LocalNavigator.currentOrThrow.parent

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 0.dp),
            text = "Opciones",
            style = MaterialTheme.typography.titleMedium
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).clip(MaterialTheme.shapes.medium)
        ) {
            ListItem(
                headlineContent = { Text("Pedidos") },
                modifier = Modifier.clickable(enabled = true) {

                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Dirección de envío") },
                modifier = Modifier.clickable(enabled = true) {
                    navigator?.push(AddressScreen(current.uid))
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Notificaciones") },
                modifier = Modifier.clickable(enabled = true) { }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Editar perfil") },
                modifier = Modifier.clickable(enabled = true) { }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Ayuda") },
                modifier = Modifier.clickable(enabled = true) { }
            )
        }
    }

    @Composable
    private fun LegalOptions() {
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 0.dp),
            text = "Legal",
            style = MaterialTheme.typography.titleMedium
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            ListItem(
                headlineContent = { Text("Licencias de terceros") },
                modifier = Modifier.clickable { /* Sin lógica */ }
            )
        }
    }

    @Composable
    private fun LogoutButton(onClick: () -> Unit = {}) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick
            ) {
                Text(text = "Salir")
            }
        }
    }

    @Composable
    private fun _GetTabOptions(): TabOptions {
        val title = "Account"
        val icon = rememberVectorPainter(Icons.Filled.Person)

        return remember {
            TabOptions(index = 0u, title = title, icon = icon)
        }
    }

    private fun readResolve(): Any = AccountTab
}