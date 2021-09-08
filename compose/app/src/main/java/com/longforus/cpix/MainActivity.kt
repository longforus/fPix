package com.longforus.cpix

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
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
    }

    @Composable
    fun AppMainNavigation() {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavCtrl provides navController) {
            NavHost(navController, startDestination = IconScreens.Image.route) {
                // Bottom Nav
                composable(IconScreens.Image.route) {
                    SearchableScreen(navController){
                        val usePaging by viewModel.usePaging.observeAsState()
                        ContentScreen(usePaging = usePaging ?: false, imageVm = imageVm)
                    }
                }
                composable(IconScreens.Video.route) {
                    SearchableScreen(navController) {
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
                        SettingsScreen(viewModel)
                    }
                }

                composable("photo", arguments = listOf(navArgument("img") {
                    type = NavType.ParcelableType(Item::class.java)
                })) {
                    PhotoScreen(it.arguments?.getParcelable("img"), navHostController = navController)
                }

            }
            navController.graph.addAll(navController.navInflater.inflate(R.navigation.mobile_navigation))
        }
    }



    @Composable
    fun SearchableScreen(navController: NavHostController, screen: @Composable () -> Unit) {
        ScaffoldScreen(navController = navController,
            float = {
                FloatingActionButton(
                    onClick = { /*TODO*/ },
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
