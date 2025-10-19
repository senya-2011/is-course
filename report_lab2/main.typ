// Главный файл отчета
#import "title.typ": title_page

#set page(margin: 2.5cm)
#set figure(numbering: "1", supplement: [Рисунок])

// Титульная страница
#title_page(
  worktype: [По лабораторной работе №2],
  theme: [Вариант 1000],
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
  image("lab2_main_page.png", width: 100%),
  caption: [Главная страница разработанного приложения]
)
#v(0.5cm)
#figure(
  image("lab2_import_page.png", width: 100%),
  caption: [Страница импорта разработанного приложения]
)
#v(1cm)
= Исходный код

#link("https://github.com/senya-2011/is-course")[https://github.com/senya-2011/is-course]

#v(1cm)

= UML-диаграммы

#figure(
  image("class_diagram_lab2.drawio.png", width: 100%),
  caption: [UML-диаграмма классов]
)

#v(0.5cm)

#figure(
  image("package_diagram.drawio.png", width: 100%),
  caption: [UML-диаграмма пакетов разработанного приложения]
)

#v(1cm)

#pagebreak()

= Выводы по работе

В ходе выполнения лабораторной работы была успешно реализована система массового импорта объектов. Добавлены новые бизнес-правила уникальности (ограничение владельцев автомобиля, запрет дублирования имен/координат/саундтреков, проверка координат города пользователя, временные ограничения для настроения RAGE), которые реализованы на уровне приложения с использованием PostgreSQL advisory locks для предотвращения race conditions.
Реализованы блокировки на уровне транзакций и централизованная валидация с учетом исключения текущей записи при обновлении. JMeter тестирование подтвердило, что при одновременных операциях создания/обновления с одинаковыми уникальными значениями только одна операция выполняется успешно, остальные получают соответствующие ошибки валидации.
