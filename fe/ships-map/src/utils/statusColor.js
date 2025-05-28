export const getColorByStatus = (status) => {
  if (!status) return "#A1A1AA"; // default gray

  const normalized = status.toLowerCase();

  switch (normalized) {
    case "under way using engine":
    case "under way sailing":
    case "engaged in fishing":
      return "#22c55e"; // green

    case "moored":
    case "at anchor":
      return "#facc15"; // yellow

    case "not under command":
    case "restricted manoeuvrability":
    case "constrained by her draught":
    case "aground":
      return "#ef4444"; // red

    case "not defined = default (also used by ais-sart under test)":
    case "ais-sart (active)":
    case "reserved for future amendment of navigational status for ships carrying dg":
    case "reserved for future amendment of navigational status for ships carrying dangerous goods (dg)":
    case "reserved for future use":
      return "#a1a1aa"; // gray

    default:
      return "#a1a1aa";
  }
};
