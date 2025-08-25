import { useState } from "react";
import { unregisterForEvent, registerForEvent } from './restdb.jsx';

export function EventsList({ events, formObject, handleListClick, registrations, refreshRegistrations }) {
  const [filter, setFilter] = useState("all"); // all | registered | unregistered

  const isRegistered = (eventId) => {
    const reg = registrations.find(r => r.event.id === eventId);
    return reg ? reg.registration.id : null;
  };

  const handleRegisterClick = (eventId) => {
    const regId = isRegistered(eventId);
    if (regId) {
      unregisterForEvent(regId, refreshRegistrations);
    } else {
      registerForEvent(eventId, refreshRegistrations);
    }
  };

  // Apply filter
  const filteredEvents = events.filter(event => {
    if (filter === "registered") return isRegistered(event.id);
    if (filter === "unregistered") return !isRegistered(event.id);
    return true; // all
  });

  return (
    <div className="boxed">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h4>Events List</h4>
        <div className="filter-buttons">
          <button 
            onClick={() => setFilter("all")} 
            className={filter === "all" ? "active" : ""}
          >
            All
          </button>
          <button 
            onClick={() => setFilter("registered")} 
            className={filter === "registered" ? "active" : ""}
          >
            Registered
          </button>
          <button 
            onClick={() => setFilter("unregistered")} 
            className={filter === "unregistered" ? "active" : ""}
          >
            Unregistered
          </button>
        </div>
      </div>

      <table id="event-list">
        <thead>
          <tr>
            <th>Title</th>
            <th>Date</th>
            <th>Location</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {filteredEvents.map((item) => (
            <tr
              key={item.id}
              className={item.id === formObject.id ? "selected" : ""}
              onClick={() => handleListClick(item)}
            >
              <td>{item.title}</td>
              <td>{item.eventDateTime}</td>
              <td>{item.location}</td>
              <td>
                {item.id === formObject.id && (
                  <button onClick={(e) => { e.stopPropagation(); handleRegisterClick(item.id); }}>
                    {isRegistered(item.id) ? "Unregister" : "Register"}
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
