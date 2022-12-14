package com.laba.user.common.countrypicker;

public interface CountryPickerListener {
     void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID);
}
