# DIS 2 project by team DIStopia

## Project introduction

We plan on implementing a widget set for home automation, with respect to the following three goals:

1. Respect the 10 golden rules of interface design and the guidlines of the UI toolkit of the development platform
2. Provide natural mappings from the widgets to the controlled devices
3. Be usable with one hand and possibly without looking (just like a remote)
4. Perform 90% of the controls for 90% of the things at home

The usage scenario will be as follows:

* *Context*: At home
* *Domain*: Home automation
* *Users*: Tech-savvy persons between 20 and 50 (early majority)

## Widgets

We will implement a set of different widgets to control the most used devices at home, which include the TV, radio, music, loudspeaker, timers, movable objects (such as shutters), etc..

* *Banner slider widget:* Will allow to slide through an ordered set of channels or sequential data, with a image representing the controlled device in the middle. The device should be able to select exactly _one_ of those datapoint simultaneaously. _Usage scenario:_ TV, radio.
* *Flower widget:* Will allow to select a discrete range of values in a circular fashion. It highlights the currently selected values for a good visibility. _Usage scenario:_ Menus.
* *Absolute circle widget:* Will allow to select a a range inside a circle. It highlights the currently select subpart of the circle. _Usage scenario:_ Timer, percentages.
* *Absolute regulator widget:* Will allow to horizontally or vertically regulate objects using a slider. _Usage scenario:_ Shutter, temperature.
* *Radial control widget:* Will allow the user to lower and raise a level. It displays radial lines in order to give feedback of the current level. _Usage scenario:_ Volume control.
* *Freeform button:* Just a simple button with an arbitrary background. _Usage scenario:_ Turning things on and off, e.g. lights.

The widgets will be implemented in thise repository.
