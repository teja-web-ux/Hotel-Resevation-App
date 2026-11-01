# Star Hotel – Hotel Reservation System

A front-end only hotel booking website built with HTML5, CSS3 and vanilla JavaScript. Data is stored in the browser's LocalStorage, so there is no server, database or build step. Built as a college final-year project.

## How to run
1. Unzip the folder.
2. Double-click `index.html` (or right-click, Open with Chrome/Edge/Firefox).
3. An internet connection is needed for Google Fonts, Font Awesome icons and the Unsplash room photos.

Optional: for a local server, run `python -m http.server 8000` in this folder and open http://localhost:8000.

## Demo credentials
| Role  | Email                   | Password |
|-------|-------------------------|----------|
| Guest | demo@starhotel.com      | demo123  |
| Admin | admin@hoteleasy.com     | admin123 |
| Admin | admin@starhotel.com     | admin123 |

The login page also has one-click "Demo guest" and "Demo admin" buttons. You can register new accounts too.

## Features
- Search rooms by dates, guests, type, price and keyword; sort by price
- Room details with image gallery, amenities and availability check
- Booking form with validation, automatic night count and total with 12% tax
- Overlapping-date protection: a room cannot be double-booked
- Demo payment (Card, UPI, Net Banking, Cash at Hotel); no real payment is made
- My Bookings with status, pay-now and cancel (cancelling frees the room)
- Login, register, logout, remember me, protected pages, admin-only dashboard
- Admin: statistics, revenue by room, booking status changes, add/edit/delete rooms, users list
- Responsive layout, mobile menu, preloader, toasts, modals, empty states

## Booking statuses
`Pending` (saved, awaiting payment) → `Confirmed` (paid or cash at hotel) → `Completed` (after check-out date). A booking can also be `Cancelled`. Pending and Confirmed bookings block the room.

## Folder structure
```
StarHotel/
├── index.html  rooms.html  room-details.html  booking.html  payment.html
├── my-bookings.html  login.html  register.html  contact.html  admin.html
├── css/style.css
├── js/
│   ├── app.js       storage helpers, seed data, nav/footer, toast, modal, contact form
│   ├── auth.js      register, login, logout, session, page protection
│   ├── rooms.js     room cards, filters, search, room details
│   ├── booking.js   booking form, payment, My Bookings, cancellation
│   └── admin.js     dashboard, bookings, room CRUD, users
├── images/          optional local photos
└── README.md
```

## LocalStorage keys
`sh_rooms`, `sh_bookings`, `sh_users`, `sh_session`, `sh_messages`. To reset the demo, clear the site data in your browser (or run `localStorage.clear()` in the console) and reload.

## Moving to a real backend later
1. Replace the `SH.get/SH.set` calls with `fetch()` requests to a REST API (for example Node/Express, Django or Laravel).
2. Suggested endpoints: `POST /auth/register`, `POST /auth/login`, `GET /rooms`, `POST /rooms` (admin), `GET /rooms/availability?checkIn&checkOut`, `POST /bookings`, `GET /bookings/me`, `PATCH /bookings/:id/status`.
3. Hash passwords (bcrypt) and issue JWT or session cookies. The current plain-text passwords in LocalStorage are for demo only.
4. Move `SH.isFree` and `SH.price` to the server so availability and totals cannot be changed from the browser.
5. Use a real payment gateway (Razorpay, Stripe) in place of the simulated payment in `booking.js`.
6. Store rooms, users and bookings in a database (MySQL, PostgreSQL or MongoDB).

## Notes
- Security: this is a demo. Anyone can read or edit LocalStorage, so do not use real passwords or card numbers.
- The admin email from the original brief (`admin@hoteleasy.com`) is kept, and `admin@starhotel.com` also works.
