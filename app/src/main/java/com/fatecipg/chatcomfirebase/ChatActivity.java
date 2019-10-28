package com.fatecipg.chatcomfirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.icu.text.SelectFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mensagensRecyclerView;
    private ChatAdapter adapter;
    private List<Mensagem> mensagens;
    private EditText mensagemEditText;
    private FirebaseUser fireUser;
    private CollectionReference mMsgsReference;
    private ImageView pictureImageView;
    private TextView mensagemTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mensagensRecyclerView =
                findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new ChatAdapter(this, mensagens);
        mensagensRecyclerView.setAdapter(adapter);
        LinearLayoutManager llm =
                new LinearLayoutManager(this);
        mensagensRecyclerView.setLayoutManager(llm);
        mensagemEditText =
                findViewById(R.id.mensagemEditText);
        pictureImageView =
                findViewById(R.id.pictureImageView);
        mensagemTextView =
                findViewById(R.id.mensagemTextView);
    }
    private void setupFirebase (){
        fireUser = FirebaseAuth. getInstance ().getCurrentUser();
        mMsgsReference = FirebaseFirestore. getInstance ().collection("mensagens");
        getRemoteMsgs();
    }
    @Override
    protected void onStart()    {
        super.onStart();
        setupFirebase();
    }

    private void getRemoteMsgs (){
        mMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(
                    @Nullable QuerySnapshot queryDocumentSnapshots,
                    @Nullable FirebaseFirestoreException e)
            { mensagens.clear();
              for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    Mensagem m = doc.toObject(Mensagem.class);
                    mensagens.add(m);            }
                Collections. sort (mensagens);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void enviarMensagem(View view) {
        String mensagem = mensagemEditText.getText().toString();
        Mensagem m = new Mensagem (
                mensagem,
                fireUser.getEmail(),
                new Date()

        );

        mMsgsReference.add(m);

    }
}

class ChatViewHolder  extends RecyclerView.ViewHolder{
    public ImageView pictureImageView;
    TextView dataNomeTextView;
    TextView mensagemTextView;

    public ChatViewHolder(View raiz){
        super(raiz);
        dataNomeTextView =
                raiz.findViewById(R.id.dataNomeTextView);
        mensagemTextView =
                raiz.findViewById(R.id.mensagemTextView);
        pictureImageView =
                raiz.findViewById(R.id.pictureImageView);
    }
}

class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    public ChatAdapter(Context context, List<Mensagem> mensagems) {
        this.context = context;
        this.mensagems = mensagems;
    }

    private Context context;
    private List<Mensagem> mensagems;

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View raiz = inflater.inflate(
                R.layout.list_item,
                parent,
                false
        );
        return  new ChatViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensagem m = mensagems.get(position);
        holder.dataNomeTextView.setText(
                context.getString(
                        R.string.data_nome,
                        DateHelper.format(m.getData()),
                        m.getEmail()
                )
        );
        holder.mensagemTextView.setText(m.getTexto());

        //profilePicImageView
        StorageReference pictureStorageRefence =
                FirebaseStorage.getInstance().getReference(
                        String.format(
                                Locale.getDefault(),
                                "imagens/%s/profilePic.jpg",
                                m.getEmail().replace("@","")
                        )
                );
        pictureStorageRefence.getDownloadUrl().addOnSuccessListener(
                (result)->{
                    Glide.
                            with(context).
                            load(pictureStorageRefence).
                            into(holder.pictureImageView);
                }
        ).addOnFailureListener(
                (exception) ->{
                    holder.pictureImageView.setImageResource(R.drawable.ic_person_black_50dp);
                }
        );

    }

    @Override
    public int getItemCount() {
        return mensagems.size();
    }
}