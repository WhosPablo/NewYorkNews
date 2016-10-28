package com.whospablo.newyorknews.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.whospablo.newyorknews.R;
import com.whospablo.newyorknews.models.Filters;
import com.whospablo.newyorknews.util.MultiSelectionSpinner;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EditFiltersDialogFragment extends DialogFragment implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {
    public static final String TITLE = "title";
    private static final String FILTERS = "filters";

    @Override
    public void selectedIndices(List<Integer> indices) {

    }

    @Override
    public void selectedStrings(List<String> strings) {
        mFilters.news_desk_values = strings;
    }

    public interface OnApplyFiltersListener{
        public void applyFilters(Filters f);
    }

    @BindView(R.id.sort_order_spinner) Spinner mSortOrderSpinner;
    @BindView(R.id.from_edit_text) EditText mFromDateEditText;
    @BindView(R.id.to_edit_text) EditText mToDateEditText;
    @BindView(R.id.news_desk_spinner) MultiSelectionSpinner mNewDeskSpinner;


    private OnApplyFiltersListener mCallback;
    private Unbinder unbinder;
    private Filters mFilters;
    private ViewGroup mContainer;

    public EditFiltersDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditFiltersDialogFragment newInstance(String title, Filters filters, OnApplyFiltersListener callback) {
        EditFiltersDialogFragment frag = new EditFiltersDialogFragment();
        frag.setOnApplyFiltersListener(callback);
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putParcelable(FILTERS, Parcels.wrap(filters));
        frag.setArguments(args);
        return frag;
    }

    public void setOnApplyFiltersListener(OnApplyFiltersListener callback){
        this.mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContainer = container;

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder =  new  AlertDialog.Builder(getActivity());

        String title = getArguments().getString(TITLE, "Filter Search");
        dialogBuilder.setTitle(title);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(
                R.layout.fragment_edit_filters,
                mContainer);
        unbinder = ButterKnife.bind(this, view);




        mFilters = Parcels.unwrap(getArguments().getParcelable(FILTERS));
        if(mFilters == null)
            mFilters = new Filters();

        mFromDateEditText.setInputType(InputType.TYPE_NULL);
        mFromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();

                DatePickerDialog dpd = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear , int dayOfMonth) {
                                Date chosen = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                                DateFormat format = android.text.format.DateFormat.getDateFormat(getContext());
                                mFromDateEditText.setText(format.format(chosen));
                                mFilters.begin_date = android.text.format.DateFormat.format("yyyyMMdd", chosen).toString();
                                Log.d("DEBUG", mFilters.begin_date);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));

                dpd.show();
            }
        });

        mToDateEditText.setInputType(InputType.TYPE_NULL);
        mToDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();

                DatePickerDialog dpd = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear , int dayOfMonth) {
                                Date chosen = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                                DateFormat format = android.text.format.DateFormat.getDateFormat(getContext());
                                mToDateEditText.setText(format.format(chosen));
                                mFilters.end_date = android.text.format.DateFormat.format("yyyyMMdd", chosen).toString();
                                Log.d("DEBUG", mFilters.end_date);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));

                dpd.show();
            }
        });

        List<String> list = new ArrayList<>();
        list.add("Arts");
        list.add("Fashion");
        list.add("Sports");

        mNewDeskSpinner.setItems(list);
        mNewDeskSpinner.setListener(this);



        int selection = Filters.SORT_BY_NEWEST.equals(mFilters.sort)? 0:1;
        mSortOrderSpinner.setSelection(selection);

        SimpleDateFormat input = new SimpleDateFormat("yyyyMMdd");
        DateFormat format = android.text.format.DateFormat.getDateFormat(getContext());

        try{
            Date inputDate = input.parse(mFilters.begin_date);
            mFromDateEditText.setText(format.format(inputDate));

            inputDate = input.parse(mFilters.end_date);
            mToDateEditText.setText(format.format(inputDate));
        } catch (Exception e){
            e.printStackTrace();
        }

        mNewDeskSpinner.setSelection(mFilters.news_desk_values);


        dialogBuilder.setView(view);

        dialogBuilder.setPositiveButton("Apply",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mFilters.sort =
                                mSortOrderSpinner.getSelectedItemPosition() ==0?
                                        Filters.SORT_BY_NEWEST:
                                        Filters.SORT_BY_OLDEST;
                        mCallback.applyFilters(mFilters);
                    }
                }
        );

        dialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        dialogBuilder.setNeutralButton("Clear",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mFilters = new Filters();
                        mCallback.applyFilters(mFilters);
                    }
                }
        );



        return dialogBuilder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Filter Search");
        getDialog().setTitle(title);
//        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}