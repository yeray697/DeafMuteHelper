package yrj.ayudasordomudo.interfaces;

import java.util.Locale;

import yrj.ayudasordomudo.model.Idiomas;

/**
 * Created by yeray697 on 21/11/16.
 */

public interface IMvp {
    interface View {
        void setMessageError(String messageError, int idView);
    }
    interface Presenter {
        void textToSpeech(String text);
    }
}
