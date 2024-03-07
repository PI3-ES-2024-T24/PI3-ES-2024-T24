package com.puc.pi3_es_2024_t24

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.puc.pi3_es_2024_t24.databinding.ActivityHomeBinding
import org.checkerframework.checker.units.qual.A

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.bottom_locker -> openFragment(LockerFragment())
                R.id.bottom_settings -> openFragment(SettingsFragment())
                R.id.bottom_about -> openFragment(AboutFragment())
            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> {
                openFragment(HomeFragment())
            }
            R.id.nav_locker -> openFragment(LockerFragment())
            R.id.nav_settings -> openFragment(SettingsFragment())
            R.id.nav_about -> openFragment(AboutFragment())
            R.id.nav_logout ->{
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressedDispatcher
        }

    }
    private  fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
