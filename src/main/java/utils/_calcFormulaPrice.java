/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.math.BigDecimal;

/**
 *
 * @author SML-DEV-PC9
 */
public class _calcFormulaPrice {

    public BigDecimal _calprice(BigDecimal qty, BigDecimal price, String formula){
        BigDecimal total= new BigDecimal(0);
        
        total = price;
        
            if (formula.trim().length() > 0)
            {
                // = = กำหนดราคาขาย
                // + = เพิ่ม
                // - = ลด
                if (formula.charAt(0) == '=' || formula.charAt(0) == '-' || formula.charAt(0) == '+')
                {
                    if (formula.charAt(0) == '=')
                    {
                        String[] __split = formula.split(",");
                        if (__split.length > 0)
                        {
                            // เปลี่ยนราคาใหม่
                            String __priceStr = __split[0].replace("=", "");
                            price = new BigDecimal(__priceStr);
                            StringBuilder __newFormat = new StringBuilder();
                            for (int __loop = 1; __loop < __split.length; __loop++)
                            {
                                if (__newFormat.length() > 0)
                                {
                                    __newFormat.append(",");
                                }
                                __newFormat.append(__split[__loop]);
                            }
                            formula = __newFormat.toString();
                        }
                    }


                    _calcDiscountResultStruct __data = this._calcDiscountOnly(price, formula, price, 2); //fix _g.g._companyProfile._item_price_decimal =2
                    //__result._newPrice = __data._newPrice;
                    total = (formula.charAt(0) == '+') ? __data._newPrice.add(__data._discountAmount) :  __data._newPrice.subtract(__data._discountAmount); //__result._realPrice = (formula.charAt(0) == '+') ? __data._newPrice + __data._discountAmount : __data._newPrice - __data._discountAmount; // toe
                    //__result._discountWord = formula.Replace("-", "");
                    //__result._discountAmount = __data._discountAmount;
                    // if (qty <> 1 && (new BigDecimal(__result._discountWord).compareTo(new BigDecimal(0)) <> 0))
                    if (qty.compareTo(new BigDecimal(1)) != 0 
                            && (new BigDecimal(formula.replace("-", "")).compareTo(new BigDecimal(0)) != 0))
                    {
                        //__result._discountWord = (MyLib._myGlobal._decimalPhase(__result._discountWord) * qty).ToString();
                        //__result._discountAmount = MyLib._myGlobal._decimalPhase(__result._discountWord);
                    }
                    
                    /*
                    if (_g.g._companyProfile._sale_real_price)
                    {
                        __result._discountWord = "";
                        __result._discountAmount = 0.0M;
                        __result._newPrice = __result._realPrice;
                    }*/
                    
                }
                else
                {
                    total = new BigDecimal(formula);
                    //__result._newPrice = MyLib._myGlobal._decimalPhase(formula);
                    //__result._realPrice = __result._newPrice;
                }
            }
            else
            {
                // toe
                total = price;
            }
            
            
        return total;
    }

    public _calcDiscountResultStruct _calcDiscountOnly(BigDecimal price, String discountWord, BigDecimal amount, int point) {
        
        _calcDiscountResultStruct __result = new _calcDiscountResultStruct();
        BigDecimal __oldAmount = amount;
            if (discountWord.trim().length() == 0)
            {
                __result._newPrice = price;
                __result._discountAmount = new BigDecimal(0);
                return __result;
            }
            String[] __words = discountWord.replace(" ", "").replace(" ", "").replace(" ", "").replace(" ", "").split(",");
            for (int __loop = 0; __loop < __words.length; __loop++)
            {
                String __getValue = __words[__loop].toString();
                if (__getValue.length() > 0)
                {
                    __getValue = __getValue.replace("-", "");
                    if (__getValue.indexOf('%') == -1)
                    {
                        amount = amount.subtract(new BigDecimal(__getValue));
                    }
                    else
                    {
                        __getValue = __getValue.replace("%", "");
                        amount = amount.subtract((new BigDecimal(__getValue).divide(new BigDecimal(100)).multiply(amount)));
                    }
                }
            }
            __result._newPrice = price;
            __result._discountAmount = __oldAmount.subtract(amount); // ลักไก่ _round(__oldAmount - amount, point);
            return __result;
            
    }

    private class _calcDiscountResultStruct {
         /// <summary>
        /// ราคาใหม่
        /// </summary>
        public BigDecimal _newPrice = new BigDecimal(0);
        /// <summary>
        /// ราคาหลังหักส่วนลด
        /// </summary>
        public BigDecimal _realPrice = new BigDecimal(0);
        /// <summary>
        /// ส่วนลดที่คำนวณได้
        /// </summary>
        public BigDecimal _discountAmount = new BigDecimal(0);
        /// <summary>
        /// ส่วนลดสำหรับสูตร
        /// </summary>
        public String _discountWord = "";
    }

    
}
