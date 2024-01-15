# CoolDto [![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome)

This Library aims to facilitate the conversion of a DTO (Data Transfert Object) to it corresponding objet and vice
versa. It helps developers to gain in time and productivity by reducing the amount of source code.

<table>
<tr>
<td colspan="2"> Simple DTO definition to target conversion</td>
</tr>
<tr>
<td>

```java
import com.djimgou.core.cooldto.annotations.Dto;

@Dto
class MyClassDto {
    //@DtoField 
    String code;
    String name;

    public MyClassDto(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

</td>
<td>

```java


class MyClass {
    String code;
    String name;
    int age;
}
```

</td>
</tr>
</table>



<table>
<tr>
<td colspan="2">Dto with custom target field name</td>
</tr>
<tr>
<tr>
<td>

```java
import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoField;

@Dto
class MyClassDto {

    @DtoField("code")
    String customCode;

    @DtoField("name")
    String customName;

    public MyClassDto(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

</td>
<td>

```java


class MyClass {
    String code;
    String name;
    int age;
    
}
```

</td>
</tr>
</table>

## Usage:

```java

import com.djimgou.core.cooldto.service.DtoSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class MyComponent {
    @Autowired
    DtoSerializer dtoSerializer;

    void serializeDto() {
        MyDtoClass myDtoClass = new MyDtoClass("123", "John");
        MyClass myClass = new MyClass();
        dtoSerializer.serialize(myDtoClass, myClass);
        // code=123,name=John
    }
}
```

## Documentation:

- [ENG](README-ENG.md)
- [FR](README-FR.md)