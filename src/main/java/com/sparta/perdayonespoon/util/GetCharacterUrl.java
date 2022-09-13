package com.sparta.perdayonespoon.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@Getter
@NoArgsConstructor
public class GetCharacterUrl {
    private static final String yellowMandoo = "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/character/perday-yellow.png";
    private static final String pinkMandoo = "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/character/perday-pink.png";
    private static final String blueMandoo = "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/character/perday-blue.png";
    private static final String purpleMandoo = "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/character/perday-pupple.png";
    private static final String greenMandoo = "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/character/perday-green.png";

    private static final String yellowMandooColor = "#fbe5a5";
    private static final String pinkMandooColor = "#f29bca";
    private static final String blueMandooColor = "#dbb4f4";
    private static final String purpleMandooColor = "#bbdcad";
    private static final String greenMandooColor = "#b4d7fc";

    public static String getMandooUrl(int characterId){
        switch (characterId){
            case 1: return yellowMandoo;
            case 2: return pinkMandoo;
            case 3: return purpleMandoo;
            case 4: return greenMandoo;
            case 5: return blueMandoo;
        }
        return null;
    }

    public static String getMandooColor(int characterId){
        switch (characterId){
            case 1: return yellowMandooColor;
            case 2: return pinkMandooColor;
            case 3: return purpleMandooColor;
            case 4: return greenMandooColor;
            case 5: return blueMandooColor;
        }
        return null;
    }

}
