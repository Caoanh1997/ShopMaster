package com.example.caoan.shopmaster.FragmentComponent;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.example.caoan.shopmaster.Adapter.BillExpandListAdapter;
import com.example.caoan.shopmaster.Adapter.BillTransportAdapter;
import com.example.caoan.shopmaster.Model.Bill;
import com.example.caoan.shopmaster.Model.Cart;
import com.example.caoan.shopmaster.OrderActivity;
import com.example.caoan.shopmaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransportOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransportOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransportOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OrderActivity orderActivity;
    private View view;
    private HashMap<Bill, List<Cart>> billListHashMap;
    private List<Bill> billList;
    private FirebaseDatabase firebaseDatabase;
    private String userID;
    private ProgressBar progressBar;
    private ExpandableListView expandableListView;
    private BillTransportAdapter billTransportAdapter;
    private Bill item;

    public TransportOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransportOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransportOrderFragment newInstance(String param1, String param2) {
        TransportOrderFragment fragment = new TransportOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transport_order, container, false);
        expandableListView = view.findViewById(R.id.eplorder);
        progressBar = view.findViewById(R.id.progressbar);

        Load();
        return view;
    }

    public void getUserID(){
        userID = getActivity().getSharedPreferences("Account",Context.MODE_PRIVATE).getString("userID","");
    }

    private void Load() {
        billListHashMap = new HashMap<>();
        billList = new ArrayList<>();
        getUserID();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("TransportOrder").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Cart> cartList;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bill bill = snapshot.getValue(Bill.class);
                    cartList = bill.getCartList();
                    Bill b = new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),
                            bill.getPhone(), bill.getUserID(), bill.getTotal_price(),
                            bill.getState(), bill.getKey_store(), bill.getDatetime(), bill.getDatetime_delivered());

                    billList.add(b);
                    billListHashMap.put(b, cartList);

                }
                progressBar.setVisibility(View.GONE);
                if (billList.size() == 0) {
                    Crouton.makeText(getActivity(), "Không có đơn hàng", Style.ALERT).show();
                } else {
                    billTransportAdapter = new BillTransportAdapter(getContext(), billList, billListHashMap, new TransportOrderFragment());
                    expandableListView.setAdapter(billTransportAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            orderActivity = (OrderActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
