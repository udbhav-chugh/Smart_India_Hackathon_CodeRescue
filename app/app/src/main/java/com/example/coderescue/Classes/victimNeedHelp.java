package com.example.coderescue.Classes;

import org.bson.types.ObjectId;
import java.util.ArrayList;

public class victimNeedHelp {
    public final ObjectId _id;
    public final victims[] vict;

    public victimNeedHelp(ObjectId id){
        this._id = id;
        this.vict = new victims[5];
    }
}
