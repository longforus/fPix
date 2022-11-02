package com.longforus.cpix

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.longforus.cpix.bean.IconScreens
import com.longforus.cpix.bean.Item
import com.longforus.cpix.screen.FavoriteScreen
import com.longforus.cpix.screen.ContentScreen
import com.longforus.cpix.screen.PhotoScreen
import com.longforus.cpix.screen.SettingsScreen
import com.longforus.cpix.ui.theme.CPixTheme
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.util.StatusBarUtil
import com.longforus.cpix.viewmodel.ImageViewModel
import com.longforus.cpix.viewmodel.MainViewModel
import com.longforus.cpix.viewmodel.VideoViewModel
import com.permissionx.guolindev.PermissionX

val LocalNavCtrl = staticCompositionLocalOf<NavHostController?> {
    null
}

class MainActivity : AppCompatActivity() {
    private val imageVm by viewModels<ImageViewModel>()
    private val videoVm by viewModels<VideoViewModel>()
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(this)
        setContent {
            AppMainNavigation()
        }
        PermissionX.init(this).permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).request { allGranted, grantedList, deniedList ->
            if (!allGranted) {
                Toast.makeText(this, "cant download image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    fun AppMainNavigation() {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavCtrl provides navController) {
            NavHost(navController, startDestination = IconScreens.Image.route) {
                // Bottom Nav
                composable(IconScreens.Image.route) {
                    SearchableScreen(navController, {
                        navController.navigate("search?isImage=true")
                    }) {
                        val usePaging by viewModel.usePaging.observeAsState()
                        ContentScreen(usePaging = usePaging ?: false, imageVm = imageVm)
                    }
                }
                composable(IconScreens.Video.route) {
                    SearchableScreen(navController, {
                        navController.navigate("search?isImage=false")
                    }) {
                        val usePaging by viewModel.usePaging.observeAsState()
                        ContentScreen(usePaging = usePaging ?: false, imageVm = videoVm)
                    }
                }
                composable(IconScreens.Favorite.route) {
                    ScaffoldScreen(navController) {
                        FavoriteScreen()
                    }
                }
                composable(IconScreens.Setting.route) {
                    ScaffoldScreen(navController) {
                        SettingsScreen(viewModel) {
                            startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://github.com/longforus/fPix")
                            })
                        }
                    }
                }

                composable("photo", arguments = listOf(navArgument("img") {
                    type = NavType.ParcelableType(Item::class.java)
                })) {
                    PhotoScreen(it.arguments?.getParcelable("img"), navHostController = navController)
                }

                dialog("search?isImage={isImage}", arguments = listOf(navArgument("isImage") {
                    defaultValue = true
                    type = NavType.BoolType
                })) {
                    val isImage = it.arguments?.getBoolean("isImage") ?: true
                    SearchDialog(isImage, navController)
                }

            }
            navController.graph.addAll(navController.navInflater.inflate(R.navigation.mobile_navigation))
        }
    }

    @Composable
    fun SearchDialog(isImage: Boolean = true, navController: NavHostController) {
        var text by remember { mutableStateOf("") }
        var openDialog by remember { mutableStateOf(true) }
        if (!openDialog) {
            navController.navigateUp()
            return
        }
        CPixTheme {
            Dialog(onDismissRequest = {
                openDialog = false
            }) {
                Surface {
                    Column {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text(text = "Search") },
                            maxLines = 1,
                            modifier = Modifier.padding(20.dp),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    if (isImage) {
                                        imageVm.doSearch(text, viewModel.usePaging.value ?: false)
                                    } else {
                                        videoVm.doSearch(text, viewModel.usePaging.value ?: false)
                                    }
                                    openDialog = false
                                }
                            ),
                            placeholder = {
                                Text(text = "input to search")
                            }
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Button(onClick = {
                                openDialog = false
                            }) {
                                Text(text = "cancel")
                            }
                            Button(onClick = {
                                if (isImage) {
                                    imageVm.doSearch(text, viewModel.usePaging.value ?: false)
                                } else {
                                    videoVm.doSearch(text, viewModel.usePaging.value ?: false)
                                }
                                openDialog = false

                            }) {
                                Text(text = "go")
                            }
                        }
                    }
                }
            }
        }

    }


    @Composable
    fun SearchableScreen(navController: NavHostController, onFabClick: () -> Unit, screen: @Composable () -> Unit) {
        ScaffoldScreen(
            navController = navController,
            float = {
                FloatingActionButton(
                    onClick = onFabClick,
                    backgroundColor = Purple500
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            screen = screen
        )
    }

    @Composable
    fun ScaffoldScreen(navController: NavHostController, float: @Composable (() -> Unit)? = null, screen: @Composable () -> Unit) {
        val bottomNavigationItems = listOf(
            IconScreens.Image,
            IconScreens.Video,
            IconScreens.Favorite,
            IconScreens.Setting
        )
        CPixTheme {
            Scaffold(
                bottomBar = { BottomAppNavBar(navController, bottomNavigationItems) },
                content = { screen() },
                floatingActionButton = { float?.invoke() }
            )
        }
    }


    @Composable
    fun BottomAppNavBar(navController: NavHostController, bottomNavigationItems: List<IconScreens>) {
        BottomAppBar(
            backgroundColor = Color.White,
            contentColor = Purple500,
            elevation = 10.dp,
        ) {
            bottomNavigationItems.forEach { screen ->
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                BottomNavigationItem(
                    icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    label = {
                        Text(text = screen.label)
                    },
                    alwaysShowLabel = false
                )
            }
        }

    }


}
