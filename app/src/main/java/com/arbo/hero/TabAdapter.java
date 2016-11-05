package com.arbo.hero;

import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import java.util.ArrayList;


public class TabAdapter extends FragmentPagerAdapter
{

    public static final String[] TITLES = new String[] {  "刺客", "战士", "法师", "坦克","射手","辅助","所有"};
    private ArrayList<Fragment> listFragments;

    public TabAdapter(FragmentManager fm)
    {
        super(fm);
    }
    public TabAdapter(FragmentManager fm, ArrayList<Fragment> al) {
        super(fm);
        listFragments = al;
    }

    @Override
    public Fragment getItem(int arg0)
    {
        return listFragments.get(arg0);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }


    @Override
    public CharSequence getPageTitle(int position)
    {
        return TITLES[position % TITLES.length];
    }


    @Override
    public int getCount()
    {

        return listFragments.size();
    }



}
