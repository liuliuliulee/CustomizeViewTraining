package com.shabiao.joy.customizeviewtraining.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.shabiao.joy.customizeviewtraining.R
import com.shabiao.joy.customizeviewtraining.fragment.DrawColorFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(DrawColorFragment())
        //对象表达式代替匿名函数，kotlin没有匿名函数
        viewPager.adapter = object :FragmentPagerAdapter(supportFragmentManager){

            override fun getItem(position: Int): android.support.v4.app.Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }

        }
    }
}
