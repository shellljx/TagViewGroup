# TagViewGroup
Android 仿`小红书`图片标签，实现了图片标签的动画，布局，水波纹，编辑等功能，还可以自定义 Tag。[视频演示地址](http://qiniu.licrafter.com/shamuMMB29Klijx12252016215920.mp4)

This is a library of tags that are attached to the picture,you can add tags and ripple effects very easily.[Video demo](http://qiniu.licrafter.com/shamuMMB29Klijx12252016215920.mp4)

[![](https://jitpack.io/v/shellljx/TagViewGroup.svg)](https://jitpack.io/#shellljx/TagViewGroup)

![](http://qiniu.licrafter.com/ezgif.com-dc9f221590.gif)

# Important Update
1. added `TagAdapter` and make it easier to create TagViewGroup.

2. moved `AnimatorUtils` out of the library to make it clean.

# Gradle

**Step 1.** Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency
```groovy
dependencies {
	    compile 'com.github.shellljx:TagViewGroup:-SNAPSHOT'
}
```

# How to use

**1. Define in xml**
```groovy
<com.licrafter.tagview.TagViewGroup
    android:id="@+id/tagViewGroup"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:inner_radius="4dp"
    app:line_width="1dp"
    app:radius="8dp"
    app:ripple_alpha="100"
    app:ripple_radius="20dp"
    app:tilt_distance="20dp"/>
```

**2. Or in code**
```groovy
TagViewGroup tagViewGroup = new TagViewGroup(getContext());
```
**3. Add animator**
```groovy
// set hide animator ,show animator and ripple
tagViewGroup.setHideAnimator(hideAnimator).setShowAnimator(showAnimator).addRipple();
```
**4. Set tagAdapter**
```groovy
tagViewGroup.setTagAdapter(new TagAdapter() {
    @Override
    public int getCount() {
        return model.getTags().size();
    }

    @Override
    public ITagView getItem(int position) {
        return makeTagTextView(model.getTags().get(position));
    }
});

//set tagViewGroup location
tagViewGroup.setPercent(model.getPercentX(), model.getPercentY());
```

**5. NotifyDataSetChanged**
```groovy
tagViewGroup.getTagAdapter().notifyDataSetChanged();
```

**6. Handle click events**
```groovy
tagViewGroup.setOnTagGroupClickListener(new TagViewGroup.OnTagGroupClickListener() {
    @Override
    public void onCircleClick(TagViewGroup container) {
    //click the white circle of TagViewGroup         
    }

    @Override
    public void onTagClick(TagViewGroup container, ITagView tag, int position) {
    //clikc a tag of TagViewGroup
    }

    @Override
    public void onLongPress(TagViewGroup container) {
    
    }
});
```
**7. Drag TagViewGroup**
```groovy
//you can drag tagViewGroup only if you set OnTagGroupDragListener
tagViewGroup.setOnTagGroupDragListener(new TagViewGroup.OnTagGroupDragListener() {
    @Override
    public void onDrag(TagViewGroup container, float percentX, float percentY) {
                    
    }
});
```

**Attributes:**

|attr属性|description 描述|
|:---|:---|
|inner_radius|中心内圆半径|
|radius|中心外圆半径|
|line_width|线条宽度|
|v_distance|圆心到垂直折点的垂直距离|
|tilt_distance|圆心到斜线折点的垂直距离|
|ripple_alpha|水波纹起始透明度|
|ripple_maxRadius|水波纹最大半径|

# How to customize the animation

You can use the following properties in Property Animation:

|property属性|description 描述|
|:---|:---|
|LinesRatio(TagViewGroup.LINES_RATIO)|线条显现的长度占总长度的百分比|
|TagAlpha(TagViewGroup.TAG_ALPHA)|单个Tag的透明度|
|CircleRadius(TagViewGroup.CIRCLE_RADIUS)|中心圆半径|
|CircleInnerRadius(TagViewGroup.CIRCLE_INNER_RADIUS)|中心内圆半径|

# How to implement your own Tag view

**Step 1.** create a view implement `ITagView` interface.

**Step 2.** Override the following methods:

```groovy
@Override
public void setDirection(DIRECTION direction) {
    mDirection = direction;
}

@Override
public DIRECTION getDirection() {
    return mDirection;
}
```
