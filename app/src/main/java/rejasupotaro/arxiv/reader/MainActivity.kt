package rejasupotaro.arxiv.reader

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                openPaperViewFragment()
                true
            }
            R.id.navigation_dashboard -> {
                openPaperViewFragment()
                true
            }
            R.id.navigation_notifications -> {
                openPaperFindFragment()
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        openPaperViewFragment()
    }

    private fun openPaperViewFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, PaperViewFragment())
                .commit()
    }

    private fun openPaperFindFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, PaperFindFragment())
                .commit()
    }
}
