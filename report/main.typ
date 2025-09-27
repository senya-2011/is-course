// Главный файл отчета
#import "title.typ": title_page

#set page(margin: 2.5cm)
#set figure(numbering: "1", supplement: [Рисунок])

// Титульная страница
#title_page(
  worktype: [По лабораторной работе],
  theme: [Вариант 13579],
  teacher: [Тюрин Иван Николаевич],
  author: [Алхимовици Арсений],
  group: [P3310],
  date: "2025",
)

#pagebreak()

= Задание

#table(
  columns: 1,
  inset: 12pt,
  stroke: 1pt + black,
  align: left,
)[
  #text(font: ("DejaVu Sans Mono", "Liberation Mono"))[
    #raw(read("task.txt"), block: true)
  ]
]

#v(1cm)

#figure(
  image("ui_lab1.png", width: 100%),
  caption: [Пользовательский интерфейс разработанного приложения]
)

= Исходный код

#link("https://github.com/senya-2011/IS-Actions")[https://github.com/senya-2011/IS-Actions]

#v(1cm)

= UML-диаграммы

#figure(
  image("lab1_classes.png", width: 100%),
  caption: [UML-диаграмма классов]
)

#v(0.5cm)

#figure(
  image("lab1_package.png", width: 100%),
  caption: [UML-диаграмма пакетов разработанного приложения]
)

#v(1cm)

= Выводы по работе

В ходе выполнения лабораторной работы была спроектирована и реализована информационная система для управления сущностью HumanBeing с использованием Spring Boot, JPA/Hibernate и PostgreSQL.
Архитектура с разделением на уровни представления, бизнес-логики и доступа к данным повышает сопровождаемость решения. Дополнительно реализована поддержка real-time уведомлений через Server-Sent Events.
Система включает CRUD операции, поиск, валидацию данных и обеспечена тестами. Таким образом, цели лабораторной работы достигнуты, получены практические навыки моделирования, проектирования и реализации серверной ИС на базе Spring Boot, JPA/Hibernate и PostgreSQL.