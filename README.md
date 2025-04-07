<h1 align="center">Hola 👋, Somos UPCJ</br><i>(Universidad Príncipe Carlos Juan)</i></h1>
<h3 align="center">Grupo 2 de Desarrollo Web Seguro y fundadores del Aula Visual</h3>


<div align="center">
  <img src="https://github.com/DWS-2025/project-grupo-2/blob/main/content/images/logoAulaVisual.svg" alt="AulaVisual"/>
</div>  

<h3 align="center">Lenguajes && Herramientas</h3>
<p align="center"> 
  <a href="https://www.w3schools.com/css/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/css3/css3-original-wordmark.svg" alt="css3" width="40" height="40"/> </a> 
  <a href="https://git-scm.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/git-scm/git-scm-icon.svg" alt="git" width="40" height="40"/> </a> 
  <a href="https://www.w3.org/html/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/html5/html5-original-wordmark.svg" alt="html5" width="40" height="40"/> </a> 
  <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/> </a> 
</p>

<h3 align="center">Entidades</h3>
<ul>
  <li><strong>User</strong>: Representa a los usuarios del sistema (administradores, profesores y estudiantes).</li>
  <li><strong>Course</strong>: Representa los cursos disponibles en la plataforma.</li>
  <li><strong>Submission</strong>: Representa los envíos realizados por los estudiantes en los cursos.</li>
  <li><strong>Module</strong>: Representa los módulos de contenido dentro de un curso.</li>
</ul>

<h3 align="center">Relaciones</h3>
<ul>
  <li>Un <strong>User</strong> puede ser profesor de un <strong>Course</strong> (<code>@OneToOne</code>).</li>
  <li>Un <strong>User</strong> puede estar inscrito en múltiples <strong>Course</strong> como estudiante (<code>@ManyToMany</code>).</li>
  <li>Un <strong>Course</strong> puede tener múltiples <strong>User</strong> como estudiantes (<code>@ManyToMany</code>).</li>
  <li>Un <strong>User</strong> puede tener múltiples <strong>Submission</strong> (<code>@OneToMany</code>).</li>
  <li>Un <strong>Submission</strong> pertenece a un <strong>User</strong> y a un <strong>Course</strong> (<code>@ManyToOne</code>).</li>
  <li>Un <strong>Course</strong> puede tener múltiples <strong>Module</strong> (<code>@OneToMany</code>).</li>
  <li>Un <strong>Module</strong> pertenece a un <strong>Course</strong> (<code>@ManyToOne</code>).</li>
</ul>

<h3 align="center">Imágenes Asociadas</h3>
<ul>
  <li><strong>User</strong>: Imagen de perfil (<code>image</code>, <code>imageFile</code>).</li>
  <li><strong>Course</strong>: Imagen representativa del curso (<code>imageCourse</code>).</li>
</ul>

<h3 align="center">🎯 Roles y Permisos</h3>

#### 👑 Administrador (`role = 0`)
**Permisos:**
- Crear, actualizar y eliminar usuarios.
- Asignar roles a los usuarios.
- Gestionar cursos (crear, actualizar, eliminar).
- Asignar profesores a los cursos.

#### 👨‍🏫 Profesor (`role = 1`)
**Permisos:**
- Ver contenido del curso que enseña.
- Evaluar y gestionar entregas de los estudiantes.

#### 👨‍🎓 Estudiante (`role = 2`)
**Permisos:**
- Ver contenido de los cursos en los que está inscrito.
- Subir entregas para los cursos.
- Ver sus propias entregas y calificaciones.

<h4 align="center">El usuario es dueño de una submission<br/>
El curso es dueño de varios módulos<br/>
El usuario es dueño de su destino, así como el fuego lo es de su llama: nace en lo profundo, y arde por su propia voluntad. 🔥🧙‍♂️

</h4>

<h3 align="center">Diagrama de Entidades</h3>
<div align="center">
  <img src="https://github.com/DWS-2025/project-grupo-2/blob/main/content/diagrams/Database_Diagram.svg" alt="Diagrama de Base de Datos"/>
</div>

# TOP 4+1 COMMITS by Mineja2017

En el reino del código, donde cada línea es un verso de una antigua saga, te presentamos el top 5 de nuestros commits legendarios:

1. **La edad de la API**  
   *Commit:* [9537df2](https://github.com/DWS-2025/project-grupo-2/commit/9537df222fd5989173f697be5d7166ab903db266#diff-f5bcc571ffc6d7e94042ed98e6d3bd30e3e82e550a31b607ee4fee5829aab980)  
   *Mensaje:* Diversas correciones.  
   *Descripción:* En una batalla épica contra el error, se sustituyó el uso de `==` por el método mágico `.equals()`. Un ajuste sutil pero decisivo, que aseguró que la lógica de eliminación de entregas opere con la precisión de la espada de Glamdring. Marca tambien el                    inicio de la edad de la API estando su majestuosa obra terminada. 

2. **El Ritual de la Base de Datos**  
   *Commit:* [b3ea196](https://github.com/DWS-2025/project-grupo-2/commit/b3ea196586f7ae0b8923ee5c532e4e55e9d7c94f) 
   *Mensaje:* Functional Web apliation with DDBB  
   *Descripción:* Un hechizo de integración, donde la aplicación web se unificó con su base de datos. Como una alianza de las razas libres de la Tierra Media, se logró una sinergia perfecta entre el frontend y el backend.

3. **El Conjuro AJAX**  
   *Commit:* [6f43b74](https://github.com/DWS-2025/project-grupo-2/commit/6f43b74b702b7ad4042f506afabc0c057d767bb9)  
   *Mensaje:* AJAX pageable && filter  
   *Descripción:* Con la agilidad de un elfo y la precisión de un mago, este commit introdujo paginación y filtrado vía AJAX, permitiendo que los datos fluyan tan suaves como el río Anduin.

4. **La Intervención Divina**  
   *Commit:* [cfe0e37](https://github.com/DWS-2025/project-grupo-2/commit/cfe0e37dfe3751076ef951a45c00be537778b203) 
   *Mensaje:* Divine Intervention – Compile and works (Most of it)  
   *Descripción:* Una auténtica intervención celestial. En este commit, los dioses del desarrollo se manifestaron y otorgaron la bendición de compilar sin errores, transformando el caos en armonía.

5. **El Misterio del Commit Fantasma**  
   *Commit:* [afd6293](https://github.com/DWS-2025/project-grupo-2/commit/afd6293dbf3e9724b8c00e87530d7e174dc9d558) 
   *Mensaje:* Created documentation 
   *Descripción:* Del coro de Ilúvatar y los Ainur brotan Eä (El universo) y esta documentación que ayudará al futuro concilio blanco (developers) en el uso de la API previamente implementada 

¡Que estas gestas inspiren tus propias aventuras en el mundo del desarrollo!


<h3 align="center">Miembros</h3>
<div align="center">
  👨‍🎓 Jaime García González aka <a href="https://github.com/Mineja2017">Mineja2017</a>, j.garciago.2023@alumnos.urjc.es<br/>
  👨‍🎓 Victor Ruiz Muñoz aka <a href="https://github.com/Gransuti44">Granusti44</a>, v.ruiz.2022@alumnos.urjc.es
</div>
