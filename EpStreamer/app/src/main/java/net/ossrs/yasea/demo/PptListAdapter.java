package net.ossrs.yasea.demo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easiio.epstreamer.DocOpenActivity;
import com.easiio.epstreamer.R;
import com.redking.view.activity.DocOpenDemoActivity;

import java.util.List;

public class PptListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PptModel> list;
    private Activity context;

    public PptListAdapter(List<PptModel> list,Activity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        v = context.getLayoutInflater().inflate(R.layout.item_pptlist, null, false);
        RecyclerView.ViewHolder holder = null;
        holder = new MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((MyViewHolder)holder).name.setText(list.get(position).getName());
        ((MyViewHolder)holder).size.setText(list.get(position).getSize());
        ((MyViewHolder)holder).type.setImageResource(list.get(position).getType().endsWith("ppt")?R.mipmap.icon_ppt:R.mipmap.icon_pdf);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, DocOpenDemoActivity.class).putExtra("fileUrl",list.get(position).path));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, size;
        public ImageView type;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_tv);
            size = itemView.findViewById(R.id.size_tv);
            type = itemView.findViewById(R.id.type_iv);
        }
    }
}


