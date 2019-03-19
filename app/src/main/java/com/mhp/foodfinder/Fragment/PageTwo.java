package com.mhp.foodfinder.Fragment;


import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mhp.foodfinder.Adapter.Nearbyadapter;
import com.mhp.foodfinder.Model.Nearby;
import com.mhp.foodfinder.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageTwo extends Fragment {

    private List<Nearby> nearbyList;

    private RecyclerView recyclerView;
    private Nearbyadapter adapter;
    private PageTwoListener listener;
    //private DividerItemDecoration itemDecoration;

    public PageTwo() {
        // Required empty public constructor
    }

    public interface PageTwoListener {
        void moveMap(LatLng latLng);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_page_two, container, false);

        //Bundle bundle = getArguments();

        //nearbyList = new ArrayList<Nearby>();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(itemDecoration);*/

       /* if(bundle != null){

            nearbyList = (ArrayList<Nearby>)bundle.getSerializable("nearbylist");


            adapter = new Nearbyadapter(getActivity(),nearbyList,PageTwo.this);
            recyclerView.setAdapter(adapter);
            //n = nearbyList.get(2);
            //Toast.makeText(getActivity(),name[2], Toast.LENGTH_SHORT).show();

            adapter.notifyDataSetChanged();

            //Toast.makeText(getActivity(), String.valueOf(nearbyList.size()), Toast.LENGTH_SHORT).show();

        }*/


        return view;

    }

    public void setNearbyList(List<Nearby> nearbyList) {
        this.nearbyList = nearbyList;
        adapter = new Nearbyadapter(getActivity(), nearbyList, PageTwo.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recyclerViewClick(LatLng latLng) {
        listener.moveMap(latLng);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PageTwoListener) {
            listener = (PageTwoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PageTwoListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //5.4375 100.3095
    //3.15783 101.712   &maxprice=3
}
