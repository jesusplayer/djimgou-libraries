
## Table of Contents:
- [Usage](#Usage)
- [C++](#c-2)
- [Clojure](#clojure)
- [ClojureScript](#clojurescript)
- [Dart](#dart)
- [Elixir](#elixir)
- [Go](#go)
- [Haskell](#haskell)
- [Java](#java)
- [JavaScript](#javascript)
- [Julia](#julia)
- [Kotlin](#kotlin)
- [LaTeX](#latex)
- [Markdown](#markdown)
- [Perl](#perl)
- [PHP](#php)
- [Python](#python)
- [Ruby](#ruby)
- [Rust](#rust)
- [Scala](#scala)
- [Smalltalk](#smalltalk)
- [Swift](#swift)
- [TypeScript](#typescript)


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


## DTO Class definition: @Dto
Every Dto class should have the annotation `com.djimgou.core.cooldto.annotations.Dto`. If not, `DtoNoDtoClassAnotationProvidedException` shall be throwed.

```java
import com.djimgou.core.cooldto.annotations.Dto;

@Dto
public class MyDtoClass {
    String field1;
    String field2;
    ...
}
```
## Field mapping: @DtoField
It helps to define the target field name. by default any field in the DTO is mapped by its corresponding name in the target. Make sure that every fields in DTO exists it the target with the same type. if it doesn't exist, an exception shall be throwed.

if we have a Dto field name called `fieldName`:

- Good example: Dto with existing target field name
  <table>
<tr>
<tr>
<td>

```java
import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoField;

@Dto
class MyClassDto {
    String fielName;
}
```

</td>
<td>

```java

class MyClass {
    String fieldName;
}

```
</td>
</tr>
</table>

- Bad example: Dto with non existing target field name
  <table>
<tr>
<tr>
<td>

```java
import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoField;

@Dto
class MyClassDto {
    String fielName;
}
```

</td>
<td>

```java

class MyClass {
    String fieldName2;
}

```
</td>
</tr>
</table> 

```java
@Component
class MyComponent {
    @Autowired
    DtoSerializer dtoSerializer;

    void serializeDto() {
        MyDtoClass myDtoClass = new MyDtoClass("one value");
        MyClass myClass = new MyClass();
        dtoSerializer.serialize(myDtoClass, myClass);
        // throws DtoTargetEntityNotFound
    }
}
```

## C++