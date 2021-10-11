/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function changeDivContent( nameOfDiv, newContent )
{
    var div = document.getElementById( nameOfDiv );
    if( div )
    {
        div.innerHTML = newContent;
    }
}

function add_script(scriptURL, onloadCB) {
    var scriptEl    = document.createElement("script");
    scriptEl.type   = "text/javascript";
    scriptEl.src    = scriptURL;

    function calltheCBcmn() {
        onloadCB();
    }

    if(typeof(scriptEl.addEventListener) != 'undefined') {
        /* The FF, Chrome, Safari, Opera way */
        scriptEl.addEventListener('load',calltheCBcmn,false);
    }
    else {
        /* The MS IE 8+ way (may work with others - I dunno)*/
        function handleIeState() {
            if(scriptEl.readyState == 'loaded'){
                calltheCBcmn(scriptURL);
            }
        }
        var ret = scriptEl.attachEvent('onreadystatechange',handleIeState);
    }
    document.getElementsByTagName("head")[0].appendChild(scriptEl);
}
