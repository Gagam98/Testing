package mp.p02.home1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class home_ItemAdapter extends RecyclerView.Adapter<home_ItemAdapter.ItemViewHolder> {

    private List<home_Item> itemList;
    private Context context;
    private home_ItemDatabaseHelper dbHelper;

    public home_ItemAdapter(Context context, List<home_Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = new home_ItemDatabaseHelper(context);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_recent, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        final home_Item item = itemList.get(position);

        // 아이템의 제목과 내용 설정
        holder.itemTitle.setText(item.getTitle());
        holder.itemDescription.setText(item.getContent());

        // 이미지 설정
        if (item.getImageUri() != null) {
            try {
                Uri imageUri = item.getImageUri();
                holder.itemImage.setImageURI(imageUri);
            } catch (SecurityException e) {
                e.printStackTrace();
                holder.itemImage.setImageResource(R.drawable.button1); // 기본 이미지 설정
            }
        } else {
            holder.itemImage.setImageResource(R.drawable.button1); // 기본 이미지
        }

        // 하트 버튼 초기 상태 설정
        holder.heartButton.setImageResource(item.isFavorite() ? R.drawable.heart_fill : R.drawable.heart_empty);

        // 하트 버튼 클릭 이벤트
        holder.heartButton.setOnClickListener(v -> {
            boolean isFavorite = item.isFavorite();
            new Thread(() -> {
                boolean success = dbHelper.updateHeartState(item.getId(), !isFavorite);
                if (success) {
                    item.setFavorite(!isFavorite);
                    holder.heartButton.post(() -> {
                        holder.heartButton.setImageResource(item.isFavorite() ? R.drawable.heart_fill : R.drawable.heart_empty);
                    });
                }
            }).start();
        });

        // 아이템 클릭 이벤트 -> 상세 페이지로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, home_ItemDetailActivity.class);
            intent.putExtra("item_id", item.getId());
            intent.putExtra("item_title", item.getTitle() != null ? item.getTitle() : "No Title");
            intent.putExtra("item_content", item.getContent() != null ? item.getContent() : "No Content");
            if (item.getImageUri() != null) {
                intent.putExtra("item_image_uri", item.getImageUri().toString());
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void updateItems(List<home_Item> newItems) {
        this.itemList = newItems;
        notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemDescription;
        ImageButton heartButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            heartButton = itemView.findViewById(R.id.heartButton);
        }
    }
}
