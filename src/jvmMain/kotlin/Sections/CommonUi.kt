package Sections

import Functions.openFolder
import Theme.Colors.darkGrey
import Theme.Colors.grey
import Theme.Colors.red
import Theme.Fonts.Hind
import Theme.Fonts.KronaOne
import Theme.Fonts.MonomaniacOne
import Theme.Spinner
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter

@Composable
fun Search(placeholder: String, query:String, onValueChange: (String) -> Unit, content:@Composable() (RowScope.() -> Unit)? = null){

    Row(Modifier.fillMaxWidth().height(60.dp).padding(0.dp,0.dp,0.dp,10.dp).background(darkGrey), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        CustomTextField(Modifier. widthIn(200.dp,500.dp).height(35.dp).padding(50.dp,0.dp)
            .clip(RoundedCornerShape(50)).background(red.copy(alpha = 0.2f)),
            value = query , onValueChange = onValueChange , placeholder = placeholder, leadingIcon = {
                Spacer(Modifier.width(10.dp))
            })
        if(content != null)
        content()


    }
}



@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value:String,
    onValueChange:(String)->Unit,
    placeholder : String,
    paddingLeadingIconEnd: Dp = 0.dp,
    paddingTrailingIconStart: Dp = 0.dp,
    leadingIcon: (@Composable() () -> Unit)? = null,
    trailingIcon: (@Composable() () -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions(),
    maxLines : Int = 10,
    enabled:Boolean = true
) {
    // val state = rememberSaveable{ mutableStateOf("") }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (leadingIcon != null) {
            leadingIcon()
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = paddingLeadingIconEnd, end = paddingTrailingIconStart)
        ) {
            BasicTextField(
                modifier = Modifier.fillMaxWidth().padding(0.dp,0.dp,0.dp,0.dp),
                value = value,
                onValueChange = { onValueChange(it) },
                textStyle = TextStyle(fontSize = 22.sp , color = red , fontFamily = Hind),
                keyboardActions = keyboardActions,
                enabled = enabled ,
                maxLines = maxLines
            )
            if (value.isEmpty()) {
                Text( text = placeholder ,  Modifier.fillMaxWidth().padding(0.dp,0.dp,0.dp,0.dp),color = red.copy(.6f)  , fontSize = 22.sp  , fontFamily = Hind)
            }
        }
        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}



fun Modifier.shadow(
    color: Color = Color.Black,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    edgeRadiusX:Dp = 0.dp,
    edgeRadiusY:Dp = 0.dp,

    ) = then(
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL , blurRadius.toPx())///(BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }
            frameworkPaint.color = color.toArgb()

            val leftPixel = offsetX.toPx()
            val topPixel = offsetY.toPx()
            val radiusX = edgeRadiusX.toPx()
            val radiusY = edgeRadiusY.toPx()
            val rightPixel = size.width + topPixel
            val bottomPixel = size.height + leftPixel

            canvas.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                paint = paint,
                radiusX = radiusX,
                radiusY = radiusY
            )

        }
    }
)

/**
 * It is the composable for the dropDown Menu options (Delete , Stream)
 */
@Composable
fun Item(str:String , icon :String , onClick:()->Unit){
    Row(verticalAlignment = Alignment.CenterVertically , modifier = Modifier.clickable { onClick() }.padding(20.dp,10.dp)){
        Image(painter = painterResource(icon) , contentDescription = null , Modifier.size(26.dp))
        Spacer(Modifier.weight(1f))
        Text(str , fontFamily = MonomaniacOne, fontSize = 19.sp , color = red )

    }
}


@Composable
fun TextButton(onClick:()->Unit,txt:String ,modifier:Modifier = Modifier.padding(4.dp,1.dp), fSize:Int = 19 , imgPath:String){
    Button(onClick = onClick, modifier = modifier.border(1.dp,red, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp)) , colors = ButtonDefaults.buttonColors(
        grey)){
        Row(verticalAlignment = Alignment.CenterVertically){
            if(imgPath.isBlank()) Row {
                Spinner(20.dp, 3.dp)
                Spacer(Modifier.width(5.dp))
            }
            else Image(painterResource(imgPath),contentDescription = null,Modifier.size(25.dp).padding(0.dp,0.dp,5.dp,0.dp))
            Text(txt, color = red , fontFamily = MonomaniacOne , fontSize = fSize.sp)
        }


    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmDialog(onYes:(String)->Unit,onNo:()->Unit,str1:String,str2:String , visible:Boolean){
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(Color.Black.copy(.8f)) .clickable { onNo() }, verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally){
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .size(600.dp, 350.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(darkGrey)
        ){
            Column( modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)) {
                Row( verticalAlignment = Alignment.CenterVertically){
                    Spacer(Modifier.width(15.dp))
                    Image(painterResource("icons/folder2.png"),contentDescription = null, Modifier.size(30.dp))
                    Spacer(Modifier.width(20.dp))
                    Text( str1, fontFamily = Hind , fontWeight = FontWeight.Bold , fontSize = 24.sp ,color = Color.White)
                }
                Divider(color = Color.White, thickness = 1.dp , modifier = Modifier.padding(0.dp,6.dp))
                Row(){
                    Text(str2 , fontFamily = Hind , fontWeight = FontWeight.Normal ,  fontSize = 18.sp ,color = Color.White.copy(.5f))
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(40.dp))
                Row( verticalAlignment = Alignment.CenterVertically){
                    CustomTextField(Modifier.weight(1f).height(50.dp).padding(0.dp,0.dp,0.dp,0.dp)
                        .clip(RoundedCornerShape(20.dp)).background(red.copy(alpha = 0.1f)),
                        value = query , onValueChange = {query = it} , placeholder = "Paste path or open", leadingIcon = {
                            Spacer(Modifier.width(20.dp))
                        } , enabled = true)
                    Spacer(Modifier.width(10.dp))
                    SimpleButton({ query = openFolder()?:query },"SELECT", Modifier.padding(4.dp,0.dp).clip(RoundedCornerShape(14.dp)),17)
                }

                Spacer(modifier = Modifier.weight(1f))
                Row(Modifier.padding(10.dp,5.dp)){
                    var trigr by remember { mutableStateOf(true) }
                    val clr = animateColorAsState(if (trigr)red else red.copy(.7f))
                    Button(onClick = { if(query.isNotBlank())onYes(query) } , colors = ButtonDefaults.buttonColors(backgroundColor = clr.value) , shape =
                        RoundedCornerShape(13.dp) , modifier = Modifier.fillMaxWidth().onPointerEvent(PointerEventType.Enter){ trigr =false}
                        .onPointerEvent(PointerEventType.Exit){ trigr = true}
                    ) {
                        Text("SAVE PATH",color = darkGrey,fontWeight = FontWeight.Bold , fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

