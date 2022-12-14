package com.laba.user.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.laba.user.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import es.dmoral.toasty.Toasty;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {
    private static final String TAG = "PlacesAutoAdapter";
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();

    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private final PlacesClient placesClient;
    private ClickListener clickListener;
    AutocompleteSessionToken token;

    public PlacesAutoCompleteAdapter(Context context, PlacesClient placesClient) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        token = AutocompleteSessionToken.newInstance();
        this.placesClient = placesClient;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void place(Place place);
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
    }


    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setLocationBias(mBounds)
                .setCountry("IN")
                //.setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (com.google.android.libraries.places.api.model.AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    String listOfWords = prediction.getFullText(STYLE_NORMAL).toString();

                    int index = listOfWords.lastIndexOf(",");
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), listOfWords.substring(0, index)));
                //    for (var i=0;i<resultList.size();i++) {

                 //   }
                    for(int i = 0 ; i < resultList.size() ; i++){
                        if("Cochin International Airport (COK)".equalsIgnoreCase(String.valueOf(resultList.get(i)))){
                            resultList.remove(i);
                        }
                    }

                }

            return resultList;
        } else {
            Toasty.info(mContext, "Unable to find address drag pin yourself.", Toast.LENGTH_SHORT).show();
            return resultList;
        }

    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.list_item_location, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
        mPredictionHolder.address.setText(mResultList.get(i).address);
        mPredictionHolder.area.setText(mResultList.get(i).area);
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView address, area;
        private LinearLayout mRow;

        PredictionHolder(View itemView) {
            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
            mRow = itemView.findViewById(R.id.item_view);
            mRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PlaceAutocomplete item = mResultList.get(getAdapterPosition());
            String placeId = String.valueOf(item.placeId);

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).setSessionToken(token).build();

            placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                Place place = response.getPlace();

                clickListener.place(place);
            }).addOnFailureListener(exception -> {
                if (exception instanceof ApiException) {
                    Toast.makeText(mContext, "" + exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence address, area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @Override
        public String toString() {
            return area.toString();
        }
    }
}