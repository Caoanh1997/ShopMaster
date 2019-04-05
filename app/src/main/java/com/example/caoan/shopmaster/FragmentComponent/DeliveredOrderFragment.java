package com.example.caoan.shopmaster.FragmentComponent;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.caoan.shopmaster.Adapter.BillDeliveredAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeliveredOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeliveredOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveredOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OrderActivity orderActivity;
    private ExpandableListView expandableListView;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private List<Bill> billList;
    private HashMap<Bill, List<Cart>> billListHashMap;
    private View view;
    private String userID;
    private BillDeliveredAdapter billDeliveredAdapter;
    private FloatingSearchView floatingSearchView;
    private RadioGroup rdgsearch;
    private RadioButton rdbname, rdbdate;
    private LinearLayout search_date;
    private TextView tvdate;
    private Button btpick_date;
    private RelativeLayout layout_search;
    private TextView tvnumber_order;

    public DeliveredOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveredOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveredOrderFragment newInstance(String param1, String param2) {
        DeliveredOrderFragment fragment = new DeliveredOrderFragment();
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
        view = inflater.inflate(R.layout.fragment_delivered_order, container, false);
        expandableListView = view.findViewById(R.id.eplorder);
        progressBar = view.findViewById(R.id.progressbar);
        floatingSearchView = view.findViewById(R.id.floating_search_view);
        rdgsearch = view.findViewById(R.id.rdgsearch);
        rdbname = view.findViewById(R.id.rdbname);
        rdbdate = view.findViewById(R.id.rdbdate);
        search_date = view.findViewById(R.id.search_date);
        tvdate = view.findViewById(R.id.tvdate);
        btpick_date = view.findViewById(R.id.btpick_date);
        layout_search = view.findViewById(R.id.layout_search);
        tvnumber_order = view.findViewById(R.id.tvnumber_order);

        btpick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new ConfirmOrderFragment.DatePickerFragment(tvdate, billDeliveredAdapter);
                dialogFragment.show(getFragmentManager(), "Date Picker");
            }
        });


        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                billDeliveredAdapter.getFilter().filter(newQuery);
            }
        });

        Load();
        rdgsearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rdbname:
                        billDeliveredAdapter.getFilter().filter(null);
                        floatingSearchView.setVisibility(View.VISIBLE);
                        search_date.setVisibility(View.GONE);
                        break;
                    case R.id.rdbdate:
                        billDeliveredAdapter.getFilter().filter(null);
                        floatingSearchView.setVisibility(View.GONE);
                        search_date.setVisibility(View.VISIBLE);
                        break;
                    default:
                        floatingSearchView.setVisibility(View.VISIBLE);
                        search_date.setVisibility(View.GONE);
                        break;
                }
            }
        });
        return view;
    }

    public void getUserID(){
        userID = getActivity().getSharedPreferences("Account",Context.MODE_PRIVATE).getString("userID","");
    }

    private void Load() {
        billList = new ArrayList<>();
        billListHashMap = new HashMap<>();
        getUserID();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("DeliveredOrder").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Cart> cartList;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Bill bill = snapshot.getValue(Bill.class);
                    cartList = bill.getCartList();
                    Bill b = new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),
                            bill.getPhone(),bill.getUserID(),bill.getTotal_price(),bill.getState(),
                            bill.getKey_store(),bill.getDatetime(),bill.getDatetime_delivered());
                    billList.add(b);
                    billListHashMap.put(b,cartList);
                }
                progressBar.setVisibility(View.GONE);
                if (billList.size() == 0) {
                    tvnumber_order.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(tvnumber_order);
                    layout_search.setVisibility(View.GONE);
                } else {
                    billDeliveredAdapter = new BillDeliveredAdapter(billList, billListHashMap, getContext(), new DeleteOrderFragment());
                    expandableListView.setAdapter(billDeliveredAdapter);
                    tvnumber_order.setVisibility(View.VISIBLE);
                    tvnumber_order.setText("Có " + String.valueOf(billList.size()) + " đơn hàng");
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(tvnumber_order);
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
