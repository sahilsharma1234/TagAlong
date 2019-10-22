package com.carpool.tagalong.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ModelDocuments;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.MyViewHolder> {

    private List<ModelDocuments> documentsList;
    private Activity context;
    private drivinginteraction drivinginteraction;

    public DocumentListAdapter(Activity context, List<ModelDocuments> documentsList, drivinginteraction drivinginteraction) {

        this.documentsList = documentsList;
        this.context       = context;
        this.drivinginteraction = drivinginteraction;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.uploaded_document_review, viewGroup, false);
        return new DocumentListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        final int pos = i;
//
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                myViewHolder.progressBar.setVisibility(View.VISIBLE);

                Glide.with(context).asBitmap().load(documentsList.get(i).getUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Bitmap>() {

                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                          Toast.makeText(context, "Some Error occurs!!", Toast.LENGTH_SHORT).show();
                                          myViewHolder.progressBar.setVisibility(View.GONE);
                                          return false;
                                      }

                                      @Override
                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                          setImage(myViewHolder, bitmap);
                                          return false;
                                      }
                                  }
                        ).submit();
            }
        },0);

        myViewHolder.delete_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteAlertDialog("Delete Document", true, 0, documentsList.get(pos).get_id(), pos);
            }
        });
    }

    private void setImage(final MyViewHolder viewHolder, final Bitmap bitmap) {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewHolder.document_image.setImageBitmap(reduceImageAndSet(bitmap));
                viewHolder.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private Bitmap reduceImageAndSet(Bitmap bitmap) {
        // we'll start with the original picture already open to a file

        try {

            int origWidth  = bitmap.getWidth();
            int origHeight = bitmap.getHeight();

            final int desHeight = 250;//or the width you need

            if (origHeight > desHeight) {
                // picture is wider than we want it, we calculate its target height
                int destWidth = origWidth / (origHeight / desHeight);
                // we create an scaled bitmap so it reduces the image, not just trim it
                Bitmap b2 = Bitmap.createScaledBitmap(bitmap, destWidth, desHeight, false);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                // compress to the format you want, JPEG, PNG...
                // 70 is the 0-100 quality percentage
                b2.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                return b2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView document_image, delete_document, refresh_document;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            // title = (com.carpool.tagalong.views.RegularTextView) view.findViewById(R.id.title);
            document_image     = view.findViewById(R.id.document);
            delete_document    = view.findViewById(R.id.delete);
            refresh_document   = view.findViewById(R.id.reload);
            progressBar        = view.findViewById(R.id.documentBar);

        }
    }

    private void showDeleteAlertDialog(String title, boolean cancelable, int code, final String docId, final int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage("Are you sure you want to delete this document?");
        builder.setCancelable(cancelable);

        if (code == 0) {
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drivinginteraction.deleteDocument(docId, index);
                    dialog.cancel();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.show();
    }

    public interface drivinginteraction{

        void deleteDocument(String documentId,int index);
    }
}