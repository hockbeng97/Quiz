package application.example.clicker;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchFilterRecycler extends RecyclerView.Adapter <SearchFilterRecycler.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerAdapter";
    List<String> quizList;
    List<String> quizListAll;
    List<String> descriptionList;
    List<String> quizIdList;
    String userId;

    public SearchFilterRecycler(List<String> quizList, List<String> descriptionList, List<String> quizIdList, String userId) {
        this.quizList = quizList;
        quizListAll = new ArrayList<>();
        quizListAll.addAll(quizList);
        this.descriptionList = descriptionList;
        this.quizIdList = quizIdList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_search, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.quizTitle.setText(quizList.get(position));
        holder.txtDescription.setText(descriptionList.get(position));

        holder.view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewQuiz1(v, quizList.get(position), descriptionList.get(position), quizIdList.get( position ));
            }
        } );

    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    @Override
    public Filter getFilter() {

        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<String> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(quizListAll);
            } else {
                for (String quiz: quizListAll) {
                    if (quiz.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(quiz);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            quizList.clear();
            quizList.addAll((Collection<? extends String>) filterResults.values);
            notifyDataSetChanged();
        }
    };



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        TextView quizTitle;
        TextView txtDescription;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;

            quizTitle = itemView.findViewById( R.id.quizTitle );
            txtDescription = itemView.findViewById( R.id.txtDescription );
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), quizList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }

    public void openViewQuiz1(View view, String title, String description, String id){
        Intent intent = new Intent (view.getContext(), ViewQuiz1.class);
        intent.putExtra( "title", title);
        intent.putExtra("description", description);
        intent.putExtra( "quizId", id );
        intent.putExtra( "userId", userId );
        view.getContext().startActivity(intent);
    }
}