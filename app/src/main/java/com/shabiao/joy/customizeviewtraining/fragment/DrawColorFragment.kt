package com.shabiao.joy.customizeviewtraining.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shabiao.joy.customizeviewtraining.R

/**
 * Created by joy on 2017/7/22.
 */
class DrawColorFragment:Fragment {

    constructor()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_draw_color,container,false)
    }

}